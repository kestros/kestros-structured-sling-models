/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.kestros.commons.structuredslingmodels.utils;

import static org.apache.jackrabbit.JcrConstants.JCR_CONTENT;
import static org.apache.jackrabbit.JcrConstants.JCR_PRIMARYTYPE;

import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.BaseSlingModel;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.MatchingResourceTypeNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.structuredslingmodels.exceptions.NoParentResourceException;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.factory.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Util Class for getting Sling Models that extend BaseResource. Looks to the Model's {@link Model}
 * resourceType value to determine if the Resource is valid for the specified Class.
 */
public final class SlingModelUtils {

  private static final Logger LOG = LoggerFactory.getLogger(SlingModelUtils.class);

  private static final String PREFIX_LIBS = "/libs/";
  private static final String PREFIX_APPS = "/apps/";
  private static final String RESOURCE_TYPE_SYNTHETIC = "sling:syntheticResourceProviderResource";

  /**
   * Default Constructor.
   */
  private SlingModelUtils() {
  }

  /**
   * Adapts the passed Resource to the specified type.  The type must extend BaseResource.
   *
   * @param resource Resource to adapt.
   * @param type Class to adapt the Resource to. Class must extend BaseResource and have the
   * @param <T> Class to adapt the Resource to. Class must extend BaseResource and have the
   * @return The current Resource, adapted the specified type.
   * @throws InvalidResourceTypeException thrown when the Resource cannot be adapted to the
   *     specified type, due to a mismatch between the resourceType of the Resource, and the
   *     resourceType value of the type's {@link Model} annotation.
   */
  @Nonnull
  public static <T extends BaseResource> T adaptTo(@Nonnull final Resource resource,
      @Nonnull final Class<T> type) throws InvalidResourceTypeException {
    final String resourcePath = resource.getPath();

    if (isValidResourceType(resource, type)) {
      final T model = resource.adaptTo(type);

      if (model != null) {
        return model;
      } else {
        LOG.debug("Unable to adapt {} to {} due to null response. "
                  + " checking jcr:content resource, if it exists.", resourcePath,
            type.getSimpleName());
      }
    }

    LOG.trace("Unable to adapt {} to {} due InvalidResourceType. Attempting to adapt jcr:content "
              + "resource, if it exists", resourcePath, type.getSimpleName());

    try {
      // Calling getChildAsType to catch exception if jcr:content is not found.
      getChildAsType(JCR_CONTENT, resource, type);
      final T model = resource.adaptTo(type);
      if (model != null) {
        return model;
      } else {
        LOG.trace("Unable to adapt {} jcr:content to {} due to null response.", resourcePath,
            type.getSimpleName());
      }

    } catch (final ChildResourceNotFoundException exception) {
      LOG.trace(
          "Unable to find jcr:content resource to fall back to while adapting {} to {}, throwing "
          + "InvalidResourceTypeException", resourcePath, type.getSimpleName());
    }

    throw new InvalidResourceTypeException(resourcePath, type);
  }

  /**
   * This method is functionally the same as {@link #adaptTo(Resource, Class)} but accepts {@link
   * BaseResource} instead of {@link Resource}.
   *
   * @param baseResource Resource to adapt.
   * @param type Class to adapt the Resource to. Class must extend BaseResource and have the
   * @param <T> Class to adapt the Resource to. Class must extend BaseResource and have the
   * @return The current Resource, adapted the specified type.
   * @throws InvalidResourceTypeException thrown when the Resource cannot be adapted to the
   *     specified type, due to a mismatch between the resourceType of the Resource, and the
   *     resourceType value of the type's {@link Model} annotation.
   */
  @Nonnull
  public static <T extends BaseResource> T adaptTo(@Nonnull final BaseResource baseResource,
      @Nonnull final Class<T> type) throws InvalidResourceTypeException {
    return adaptTo(baseResource.getResource(), type);
  }

  /**
   * Adapts the passed Resource to a BaseResource Model.  Use this instead of adaptTo when adapting
   * to BaseResource to avoid impossible InvalidResourceType exceptions.
   *
   * @param resource Resource to adapt.
   * @return The current Resource, adapted to a BaseResource Model.
   */
  @Nonnull
  public static BaseResource adaptToBaseResource(@Nonnull final Resource resource) {
    try {
      return adaptTo(resource, BaseResource.class);
    } catch (final InvalidResourceTypeException exception) {
      throw new IllegalStateException();
    }
  }

  /**
   * This method is functionally the same as {@link #adaptToBaseResource(Resource)} but accepts
   * {@link BaseResource} instead of {@link Resource}.
   *
   * @param baseResource Resource to adapt.
   * @return The current Resource, adapted to a BaseResource Model.
   */
  @Nonnull
  public static BaseResource adaptToBaseResource(@Nonnull final BaseResource baseResource) {
    return adaptToBaseResource(baseResource.getResource());
  }

  /**
   * The specified child Resource adapted to the specified type, if it is valid. Will *
   * automatically check the jcr:content resource is no immediate child is found ( and the *
   * jcr:content resource is).
   *
   * @param childName Name of child Resource to retrieve.
   * @param resource Resource to retrieve child from.
   * @param type Class to adapt the child to. Class must extend BaseResource and have the {@link
   *     Model} annotation, with the resourceType value set.
   * @param <T> Class to adapt the child to. Class must extend BaseResource and have the {@link
   *     Model} annotation, with the resourceType value set.
   * @return The specified child Resource adapted to the specified type, if it is valid. Will
   *     automatically check the jcr:content resource is no immediate child is found ( and the
   *     jcr:content resource is).
   * @throws InvalidResourceTypeException thrown when the child Resource cannot be adapted to
   *     the specified type, due to a mismatch between the resourceType of the Resource, and the
   *     resourceType value of the type's {@link Model} annotation.
   * @throws ChildResourceNotFoundException thrown when the specified childBaseResource to
   *     retrieve child of. Resource does not exist, or cannot be found (possibly due to
   *     permissions).
   */
  @Nonnull
  public static <T extends BaseResource> T getChildAsType(@Nonnull final String childName,
      @Nonnull final Resource resource, @Nonnull final Class<T> type)
      throws InvalidResourceTypeException, ChildResourceNotFoundException {
    return adaptTo(getChildAsBaseResource(childName, resource), type);
  }

  /**
   * This method is functionally the same as {@link #getChildAsType(String, Resource, Class)} but
   * accepts {@link BaseResource} instead of {@link Resource}.
   *
   * @param childName Name of child Resource to retrieve.
   * @param baseResource Resource to retrieve child from.
   * @param type Class to adapt the child to. Class must extend BaseResource and have the {@link
   *     Model} annotation, with the resourceType value set.
   * @param <T> Class to adapt the child to. Class must extend BaseResource and have the {@link
   *     Model} annotation, with the resourceType value set.
   * @return The specified child Resource adapted to the specified type, if it is valid. Will
   *     automatically check the jcr:content resource is no immediate child is found ( and the
   *     jcr:content resource is).
   * @throws InvalidResourceTypeException thrown when the child Resource cannot be adapted to
   *     the specified type, due to a mismatch between the resourceType of the Resource, and the
   *     resourceType value of the type's {@link Model} annotation.
   * @throws ChildResourceNotFoundException thrown when the specified childBaseResource to
   *     retrieve child of. Resource does not exist, or cannot be found (possibly due to
   *     permissions).
   */
  @Nonnull
  public static <T extends BaseResource> T getChildAsType(@Nonnull final String childName,
      @Nonnull final BaseResource baseResource, @Nonnull final Class<T> type)
      throws InvalidResourceTypeException, ChildResourceNotFoundException {
    return getChildAsType(childName, baseResource.getResource(), type);
  }

  /**
   * Specified child Resource, as a BaseResource.
   *
   * @param childName name of the child Resource to return.
   * @param resource Resource to retrieve the child from.
   * @return Specified child Resource, as a BaseResource.
   * @throws ChildResourceNotFoundException No resource with the specified name is found.
   */
  @Nonnull
  public static BaseResource getChildAsBaseResource(@Nonnull final String childName,
      @Nonnull final Resource resource) throws ChildResourceNotFoundException {

    if (StringUtils.isNotBlank(childName)) {
      final Resource child = resource.getChild(childName);
      if (child != null) {
        return adaptToBaseResource(child);
      } else {
        final Resource jcrContent = resource.getChild(JCR_CONTENT);
        if (jcrContent != null) {
          return getChildAsBaseResource(childName, jcrContent);
        }
      }
      throw new ChildResourceNotFoundException(childName, resource.getPath());
    } else {
      throw new ChildResourceNotFoundException(childName, resource.getPath(),
          "Child name not specified.");
    }
  }

  /**
   * This method is functionally the same as {@link #getChildAsBaseResource(String, Resource)} but
   * accepts {@link BaseResource} instead of {@link Resource}.
   *
   * @param childName name of the child Resource to return.
   * @param baseResource Resource to retrieve the child from.
   * @return Specified child Resource, as a BaseResource.
   * @throws ChildResourceNotFoundException No resource with the specified name is found.
   */
  @Nonnull
  public static BaseResource getChildAsBaseResource(@Nonnull final String childName,
      @Nonnull final BaseResource baseResource) throws ChildResourceNotFoundException {
    return getChildAsBaseResource(childName, baseResource.getResource());
  }

  /**
   * List of all children, adapted to BaseResource.
   *
   * @param resource Resource to retrieve children from.
   * @return List of all children, adapted to BaseResource.
   */
  @Nonnull
  public static List<BaseResource> getChildrenAsBaseResource(@Nonnull final Resource resource) {
    final List<BaseResource> children = new ArrayList<>();
    for (final Resource child : resource.getChildren()) {
      children.add(adaptToBaseResource(child));
    }
    return children;
  }

  /**
   * This method is functionally the same as {@link #getChildrenAsBaseResource(Resource)} but
   * accepts {@link BaseResource} instead of {@link Resource}.
   *
   * @param resource Resource to retrieve children from.
   * @return List of all children, adapted to BaseResource.
   */
  @Nonnull
  public static List<BaseResource> getChildrenAsBaseResource(@Nonnull final BaseResource resource) {
    return SlingModelUtils.getChildrenAsBaseResource(resource.getResource());
  }

  /**
   * List of all valid children, adapted to the specified type. Resources that fail adaption * will
   * not be added to the List.
   *
   * @param resource Resource to retrieve children from.
   * @param type Class to adapt the children to. Class must extend BaseResource and have the
   * @param <T> Class to adapt the children to. Class must extend BaseResource and have the
   * @return List of all valid children, adapted to the specified type. Resources that fail adaption
   *     will not be added to the List.
   */
  @Nonnull
  public static <T extends BaseResource> List<T> getChildrenOfType(@Nonnull final Resource resource,
      @Nonnull final Class<T> type) {

    final List<T> children = new ArrayList<>();
    for (final Resource child : resource.getChildren()) {
      try {
        children.add(adaptTo(child, type));
      } catch (final InvalidResourceTypeException exception) {
        LOG.debug("Unable to adapt resource {} to {} due to "
                  + "InvalidResourceType while getting children" + " of {}", child.getPath(),
            type.getSimpleName(), resource.getPath());
      }

    }
    return children;
  }

  /**
   * This method is functionally the same as {@link #getChildrenOfType(Resource, Class)} )} but
   * accepts {@link BaseResource} instead of {@link Resource}.
   *
   * @param baseResource Resource to retrieve children from.
   * @param type Class to adapt the children to. Class must extend BaseResource and have the
   * @param <T> Class to adapt the children to. Class must extend BaseResource and have the
   * @return List of all valid children, adapted to the specified type. Resources that fail adaption
   *     will not be added to the List.
   */
  @Nonnull
  public static <T extends BaseResource> List<T> getChildrenOfType(
      @Nonnull final BaseResource baseResource, @Nonnull final Class<T> type) {
    return getChildrenOfType(baseResource.getResource(), type);
  }

  /**
   * Retrieves a filtered list of children, of a specified SlingModel type.
   *
   * @param resource Resource to retrieve children from.
   * @param allowedChildNames List of names that are allowed.
   * @param type Class to adapt the children to. Class must extend BaseResource and have the
   * @param <T> Class to adapt the children to. Class must extend BaseResource and have the
   * @return List of all allowed and valid children, adapted to the specified type. Resources that
   *     fail adaption will not be added to the List.
   */
  @Nonnull
  public static <T extends BaseResource> List<T> getChildrenOfType(@Nonnull final Resource resource,
      @Nonnull final List<String> allowedChildNames, @Nonnull final Class<T> type) {
    final List<T> filteredChildren = new ArrayList<>();
    for (final T child : getChildrenOfType(resource, type)) {
      if (allowedChildNames.contains(child.getName())) {
        filteredChildren.add(child);
      }
    }
    return filteredChildren;
  }

  /**
   * This method is functionally the same as {@link #getChildrenOfType(Resource, List, Class)} )}
   * but accepts {@link BaseResource} instead of {@link Resource}.
   *
   * @param baseResource Resource to retrieve children from.
   * @param allowedChildNames List of names that are allowed.
   * @param type Class to adapt the children to. Class must extend BaseResource and have the
   * @param <T> Class to adapt the children to. Class must extend BaseResource and have the
   * @return List of all allowed and valid children, adapted to the specified type. Resources that
   *     fail adaption will not be added to the List.
   */
  @Nonnull
  public static <T extends BaseResource> List<T> getChildrenOfType(
      @Nonnull final BaseResource baseResource, @Nonnull final List<String> allowedChildNames,
      @Nonnull final Class<T> type) {
    return getChildrenOfType(baseResource.getResource(), allowedChildNames, type);
  }


  /**
   * Retrieves a specified Resource as a BaseResource.
   *
   * @param resourcePath Path to the resource to retrieve.
   * @param resolver ResourceResolver
   * @return Specified Resource as a BaseResource.
   * @throws ResourceNotFoundException Throw when the specified Resource cannot be found.
   *     Resource does not exist, or cannot be found (possibly due to permissions).
   */
  @Nonnull
  public static BaseResource getResourceAsBaseResource(@Nonnull final String resourcePath,
      @Nonnull final ResourceResolver resolver) throws ResourceNotFoundException {

    if (StringUtils.isNotEmpty(resourcePath)) {
      Resource resource = resolver.getResource(resourcePath);

      if (resource != null) {
        if (RESOURCE_TYPE_SYNTHETIC.equals(resource.getResourceType())) {
          resource = resolver.getResource(PREFIX_APPS + resourcePath);
          if (resource == null || RESOURCE_TYPE_SYNTHETIC.equals(resource.getResourceType())) {
            resource = resolver.getResource(PREFIX_LIBS + resourcePath);
          }
          if (resource == null || RESOURCE_TYPE_SYNTHETIC.equals(resource.getResourceType())) {
            throw new ResourceNotFoundException(resourcePath);
          }
        }
        return adaptToBaseResource(resource);
      }
      throw new ResourceNotFoundException(resourcePath);
    }
    throw new ResourceNotFoundException(resourcePath, "Resource path not specified.");
  }

  /**
   * The specified Resource, adapted to the specified type.
   *
   * @param resourcePath Path to a Resource
   * @param resolver ResourceResolver
   * @param type Class to adapt the specified Resource to. Class must extend BaseResource and
   *     have the {@link Model} annotation, with the resourceType value set.
   * @param <T> Class to adapt the specified Resource to. Class must extend BaseResource and
   *     have the {@link Model} annotation, with the resourceType value set.
   * @return The specified Resource, adapted to the specified type.
   * @throws InvalidResourceTypeException thrown when the specified Resource is found, but
   *     cannot be adapted to the specified type, due to a mismatch between the resourceType of the
   *     Resource, and the resourceType value of the type's {@link Model} annotation.
   * @throws ResourceNotFoundException Throw when the specified Resource cannot be found.
   *     Resource does not exist, or cannot be found (possibly due to permissions).
   */
  @Nonnull
  public static <T extends BaseResource> T getResourceAsType(@Nonnull final String resourcePath,
      @Nonnull final ResourceResolver resolver, @Nonnull final Class<T> type)
      throws InvalidResourceTypeException, ResourceNotFoundException {
    return adaptTo(getResourceAsBaseResource(resourcePath, resolver), type);
  }

  /**
   * Returns a List of Resources paths adapted to the specified SlingModel type.
   *
   * @param resourcePaths Resources to lookup and adapt.
   * @param resolver Resource Resolver
   * @param type Class to adapt the specified Resource to. Class must extend BaseResource and
   *     have the {@link Model} annotation, with the resourceType value set.
   * @param <T> Class to adapt the specified Resource to. Class must extend BaseResource and
   *     have the {@link Model} annotation, with the resourceType value set.
   * @return The requested resources, adapted to the specified type.
   */
  public static <T extends BaseResource> List<T> getResourcesAsType(
      @Nonnull final List<String> resourcePaths, @Nonnull final ResourceResolver resolver,
      @Nonnull final Class<T> type) {

    final List<T> models = new ArrayList<>();
    for (final String path : resourcePaths) {
      try {
        models.add(getResourceAsType(path, resolver, type));
      } catch (final InvalidResourceTypeException exception) {
        LOG.warn("Unable to adapt {} to {} while adapting list of paths to {} due to "
                 + "InvalidResourceTypeException", path, type.getSimpleName(),
            type.getSimpleName());
      } catch (final ResourceNotFoundException exception) {
        LOG.warn("Unable to adapt {} to {} while adapting list of paths to {} due to "
                 + "ResourceNotFoundException", path, type.getSimpleName(), type.getSimpleName());
      }
    }
    return models;
  }

  /**
   * Retrieves the specified Resource's parent Resource as a BaseResource, if one exists.
   *
   * @param resource Resource to retrieve the parent of.
   * @return the specified Resource's parent Resource as a BaseResource.
   * @throws NoParentResourceException Thrown when no parent of the passed Resource can be
   *     found.
   */
  @Nonnull
  public static BaseResource getParentResourceAsBaseResource(@Nonnull final Resource resource)
      throws NoParentResourceException {
    final Resource parentResource = resource.getParent();
    if (parentResource != null) {
      return adaptToBaseResource(parentResource);
    }
    throw new NoParentResourceException(resource.getPath());
  }

  /**
   * This method is functionally the same as {@link #getParentResourceAsBaseResource(Resource)} but
   * accepts {@link BaseResource} instead of {@link Resource}.
   *
   * @param baseResource Resource to retrieve the parent of.
   * @return the specified Resource's parent Resource as a BaseResource.
   * @throws NoParentResourceException Thrown when no parent of the passed Resource can be
   *     found.
   */
  @Nonnull
  public static BaseResource getParentResourceAsBaseResource(
      @Nonnull final BaseResource baseResource) throws NoParentResourceException {
    return getParentResourceAsBaseResource(baseResource.getResource());
  }

  /**
   * The parent Resource, adapted to the specified type.
   *
   * @param resource Resource to get the parent of.
   * @param type Class to adapt the parent Resource to. Class must extend BaseResource and have
   *     the
   * @param <T> Class to adapt the parent Resource to. Class must extend BaseResource and have
   *     the
   * @return The parent Resource, adapted to the specified type.
   * @throws InvalidResourceTypeException thrown when the parent Resource is found, but  cannot
   *     be adapted to the specified type, due to a mismatch between the resourceType of the
   *     Resource, and the resourceType value of the type's {@link Model} annotation.
   * @throws NoParentResourceException thrown when the parent Resource cannot be found.  This
   *     should only happen at the root level.
   */
  @Nonnull
  public static <T extends BaseResource> T getParentResourceAsType(@Nonnull final Resource resource,
      @Nonnull final Class<T> type) throws InvalidResourceTypeException, NoParentResourceException {
    final Resource parentResource = resource.getParent();
    if (parentResource != null) {
      return adaptTo(parentResource, type);
    }
    throw new NoParentResourceException(resource.getPath());
  }


  /**
   * This method is functionally the same as {@link #getParentResourceAsType(Resource, Class)} but
   * accepts {@link BaseResource} instead of {@link Resource}.
   *
   * @param baseResource Resource to get the parent of.
   * @param type Class to adapt the parent Resource to. Class must extend BaseResource and have
   *     the
   * @param <T> Class to adapt the parent Resource to. Class must extend BaseResource and have
   *     the
   * @return The parent Resource, adapted to the specified type.
   * @throws InvalidResourceTypeException thrown when the parent Resource is found, but  cannot
   *     be adapted to the specified type, due to a mismatch between the resourceType of the
   *     Resource, and the resourceType value of the type's {@link Model} annotation.
   * @throws NoParentResourceException thrown when the parent Resource cannot be found.  This
   *     should only happen at the root level.
   */
  @Nonnull
  public static <T extends BaseResource> T getParentResourceAsType(
      @Nonnull final BaseResource baseResource, @Nonnull final Class<T> type)
      throws InvalidResourceTypeException, NoParentResourceException {

    return getParentResourceAsType(baseResource.getResource(), type);
  }

  /**
   * The first ancestor Resource that can be adapted to the specified type.
   *
   * @param resource Resource to look for ancestors of
   * @param type Class to attempt to adapt the ancestor Resource to. Class must extend
   *     BaseResource and have the {@link Model} annotation, with the resourceType value set.
   * @param <T> Class to attempt to adapt the ancestor Resource to. Class must extend
   *     BaseResource and have the {@link Model} annotation, with the resourceType value set.
   * @return The first ancestor Resource that can be adapted to the specified type.
   * @throws NoValidAncestorException thrown when ancestry ends without having found a valid
   *     Resource.
   */
  @Nonnull
  public static <T extends BaseResource> T getFirstAncestorOfType(@Nonnull final Resource resource,
      @Nonnull final Class<T> type) throws NoValidAncestorException {

    try {
      return getParentResourceAsType(resource, type);
    } catch (final InvalidResourceTypeException exception) {
      try {
        return getFirstAncestorOfType(getParentResourceAsBaseResource(resource), type);
      } catch (final NoParentResourceException | NoValidAncestorException e1) {
        throw new NoValidAncestorException(resource.getPath(), type);
      }
    } catch (final NoParentResourceException exception) {
      throw new NoValidAncestorException(resource.getPath(), type);
    }
  }

  /**
   * This method is functionally the same as {@link #getFirstAncestorOfType(Resource, Class)} but
   * accepts {@link BaseResource} instead of {@link Resource}.
   *
   * @param baseResource Resource to look for ancestors of
   * @param type Class to attempt to adapt the ancestor Resource to. Class must extend
   *     BaseResource and have the {@link Model} annotation, with the resourceType value set.
   * @param <T> Class to attempt to adapt the ancestor Resource to. Class must extend
   *     BaseResource and have the {@link Model} annotation, with the resourceType value set.
   * @return The first ancestor Resource that can be adapted to the specified type.
   * @throws NoValidAncestorException thrown when ancestry ends without having found a valid
   *     Resource.
   */
  @Nonnull
  public static <T extends BaseResource> T getFirstAncestorOfType(
      @Nonnull final BaseResource baseResource, @Nonnull final Class<T> type)
      throws NoValidAncestorException {
    return getFirstAncestorOfType(baseResource.getResource(), type);
  }

  /**
   * Traverses the JCR (using the passed Resource as the origin) to find all Resources that can be
   * adapted to the specified type.
   *
   * @param resource Resource to originate JCR traversal from.
   * @param type Class to adapt the descendants to. Class must extend BaseResource and have the
   *     {@link Model} annotation, with the resourceType value set.
   * @param <T> Class to adapt the descendants to. Class must extend BaseResource and have the
   *     {@link Model} annotation, with the resourceType value set.
   * @return List of all descendant Resources that can be adapted to the specified type, as the
   *     specified type.
   */
  @Nonnull
  public static <T extends BaseResource> List<T> getAllDescendantsOfType(
      @Nonnull final Resource resource, @Nonnull final Class<T> type) {

    final List<T> descendants = getChildrenOfType(resource, type);

    for (final BaseResource child : getChildrenOfType(resource, BaseResource.class)) {
      descendants.addAll(getAllDescendantsOfType(child.getResource(), type));
    }

    return descendants;
  }

  /**
   * This method is functionally the same as {@link #getAllDescendantsOfType(Resource, Class)} but
   * accepts {@link BaseResource} instead of {@link Resource}.
   *
   * @param baseResource BaseResource to originate JCR traversal from.
   * @param type Class to adapt the descendants to. Class must extend BaseResource and have the
   *     {@link Model} annotation, with the resourceType value set.
   * @param <T> Class to adapt the descendants to. Class must extend BaseResource and have the
   *     {@link Model} annotation, with the resourceType value set.
   * @return List of all descendant Resources that can be adapted to the specified type, as the
   *     specified type.
   */
  @Nonnull
  public static <T extends BaseResource> List<T> getAllDescendantsOfType(
      @Nonnull final BaseResource baseResource, @Nonnull final Class<T> type) {
    return getAllDescendantsOfType(baseResource.getResource(), type);
  }

  /**
   * Adapts the passed Resource to the closest matching SlingModel type that extends BaseResource.
   *
   * @param resource Resource to adapt.
   * @param modelFactory modelFactory used to match the model type to the Resource's
   *     resourceType.
   * @param <T> Generic class that extends BaseResource.
   * @return The passed Resource adapted to the closest matching SlingModel type that extends
   *     BaseResource.
   * @throws MatchingResourceTypeNotFoundException Thrown when the passed Resource cannot be
   *     dynamically adapted to a Model type.
   */
  @SuppressWarnings("unchecked")
  @Nonnull
  public static <T extends BaseResource> T getResourceAsClosestType(
      @Nonnull final Resource resource, @Nonnull final ModelFactory modelFactory)
      throws MatchingResourceTypeNotFoundException {
    Object model = null;
    try {
      model = modelFactory.getModelFromResource(resource);

    } catch (final Exception exception) {
      LOG.debug("Unable to retrieve adapted model for resource {}.", resource.getPath());
    }
    if (model instanceof BaseResource) {
      try {
        final BaseResource contentResource = getChildAsBaseResource(JCR_CONTENT, resource);
        if (modelFactory.isModelAvailableForResource(contentResource.getResource())) {
          final T contentResourceModel = (T) modelFactory.getModelFromResource(
              contentResource.getResource());
          model = adaptTo(contentResourceModel, contentResourceModel.getClass());
        }
      } catch (final InvalidResourceTypeException | ChildResourceNotFoundException exception) {
        if (resource.getPath().endsWith(JCR_CONTENT)) {
          throw new MatchingResourceTypeNotFoundException(resource.getPath());
        }
      }

      return (T) model;
    }
    throw new MatchingResourceTypeNotFoundException(resource.getPath());
  }

  /**
   * This method is functionally the same as {@link #getResourceAsClosestType(Resource,
   * ModelFactory)} but accepts {@link BaseResource} instead of {@link Resource}.
   *
   * @param resource Resource to adapt.
   * @param modelFactory modelFactory used to match the model type to the Resource's
   *     resourceType.
   * @param <T> Generic class that extends BaseResource.
   * @return The passed Resource adapted to the closest matching SlingModel type that extends
   *     BaseResource.
   * @throws MatchingResourceTypeNotFoundException Thrown when the passed Resource cannot be
   *     dynamically adapted to a Model type.
   */
  @Nonnull
  public static <T extends BaseResource> T getResourceAsClosestType(
      @Nonnull final BaseResource resource, @Nonnull final ModelFactory modelFactory)
      throws MatchingResourceTypeNotFoundException {
    return getResourceAsClosestType(resource.getResource(), modelFactory);
  }

  /**
   * Returns a List of all children, adapted to the closest matching SlingModel type that extends
   * BaseResource.
   *
   * @param resource Resource to retrieve children from.
   * @param modelFactory modelFactory used to match the model type to the Resource's
   *     resourceType.
   * @param <T> Generic class that extends BaseResource.
   * @return List of all children, adapted to the closest matching SlingModel type that extends
   */
  @Nonnull
  public static <T extends BaseResource> List<T> getChildrenAsClosestTypes(
      @Nonnull final Resource resource, @Nonnull final ModelFactory modelFactory) {
    final List<T> children = new ArrayList<>();

    for (final BaseResource child : getChildrenOfType(resource, BaseResource.class)) {
      try {
        children.add(getResourceAsClosestType(child.getResource(), modelFactory));
      } catch (final InvalidResourceTypeException exception) {
        LOG.debug("Unable to Unable to retrieve adapted model for resource {} while retrieving "
                  + "children for {}, this resource will not be included.", child.getName(),
            resource.getPath());
      }
    }

    return children;
  }

  /**
   * This method is functionally the same as {@link #getChildrenAsClosestTypes(Resource,
   * ModelFactory)} but accepts {@link BaseResource} instead of {@link Resource}.
   *
   * @param baseResource Resource to retrieve children from.
   * @param modelFactory modelFactory used to match the model type to the Resource's
   *     resourceType.
   * @param <T> Generic class that extends BaseResource.
   * @return List of all children, adapted to the closest matching SlingModel type that extends
   */
  @Nonnull
  public static <T extends BaseResource> List<T> getChildrenAsClosestTypes(
      @Nonnull final BaseResource baseResource, @Nonnull final ModelFactory modelFactory) {
    return getChildrenAsClosestTypes(baseResource.getResource(), modelFactory);
  }

  private static boolean isValidResourceTypeBasedOnSuperTypes(@Nonnull final Resource resource,
      @Nonnull final List<String> validResourceTypes) {

    final Resource resourceTypeResource = resource.getResourceResolver().getResource(
        resource.getResourceType());

    if (resourceTypeResource != null) {
      BaseResource currentResourceTypeResource = adaptToBaseResource(resourceTypeResource);

      while (currentResourceTypeResource != null) {

        if (validResourceTypes.contains(getResourceTypePath(currentResourceTypeResource))
            || validResourceTypes.contains(currentResourceTypeResource.getResourceSuperType())) {
          return true;
        }

        try {
          currentResourceTypeResource = getResourceAsType(
              currentResourceTypeResource.getResourceSuperType(), resource.getResourceResolver(),
              BaseResource.class);
        } catch (final ModelAdaptionException exception) {
          LOG.debug("Resource {} not found while checking if it is a valid resourceType based on "
                    + "superTypes. Returning false.", resource.getPath());
          currentResourceTypeResource = null;
        }
      }
    }

    return false;
  }

  /**
   * Whether the passed BaseResource is valid, based on the `resourceType` value of the type's
   * {@link Model} annotation.
   *
   * @param resource BaseResource to validate.
   * @param type Class to validate the BaseResource against. Class must extend BaseResource and
   *     have the {@link Model} annotation, with the resourceType value set.
   * @param <T> Class to validate the BaseResource against. Class must extend BaseResource and
   *     have the {@link Model} annotation, with the resourceType value set.
   * @return Whether the passed BaseResource is valid, based on the `resourceType` value of the
   *     type's {@link Model} annotation.
   */
  static <T extends BaseSlingModel> boolean isValidResourceType(@Nonnull Resource resource,
      @Nonnull final Class<T> type) {
    final List<String> validResourceTypes = Arrays.asList(
        type.getAnnotation(Model.class).resourceType());

    if (resource.getPath().startsWith(PREFIX_APPS) && JcrConstants.NT_FOLDER.equals(
        resource.getResourceType())) {
      final String libsResourcePath = resource.getPath().replaceFirst(PREFIX_APPS, PREFIX_LIBS);
      try {
        resource = getResourceAsBaseResource(libsResourcePath,
            resource.getResourceResolver()).getResource();
      } catch (final ResourceNotFoundException e) {
        LOG.trace("Attempted to retrieve /libs resource matching {}, but none could be found.",
            resource.getPath());
      }
    }
    if (validResourceTypes.contains("sling/servlet/default")) {
      return true;
    }

    if (validResourceTypes.contains(resource.getResourceType())) {
      return true;
    }
    if (validResourceTypes.contains(
        resource.getValueMap().get(JCR_PRIMARYTYPE, StringUtils.EMPTY))) {
      return true;
    }

    return isValidResourceTypeBasedOnSuperTypes(resource, validResourceTypes);
  }

  static String getResourceTypePath(final BaseResource resourceTypeResource) {
    String resourceType = resourceTypeResource.getPath();
    if (resourceType.startsWith(PREFIX_APPS)) {
      resourceType = resourceType.split(PREFIX_APPS)[1];
    }
    if (resourceType.startsWith(PREFIX_LIBS)) {
      resourceType = resourceType.split(PREFIX_LIBS)[1];
    }
    return resourceType;
  }

}
