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

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.adaptTo;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.adaptToBaseResource;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsBaseResource;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsBaseResource;

import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.filetypes.BaseFile;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for retrieving nt:file Resources as Sling Models (which extend {@link
 * BaseFile}).
 */
public class FileModelUtils {

  private static final Logger LOG = LoggerFactory.getLogger(FileModelUtils.class);

  private FileModelUtils() {
  }

  /**
   * Adapts a BaseResource to a specified Model type that extends BaseFile.  Adapted object must
   * pass validation ( no errors ).
   *
   * @param fileResource Resource to adapt.
   * @param type Model type to adapt the resource to. Must extend BaseFile.
   * @param <T> Model type to adapt the resource to. Must extend BaseFile.
   *
   * @return The Resource adapted to the specified FileType.
   * @throws InvalidResourceTypeException Thrown when the Resource cannot be adapted to the
   *         passed type, or has an error after adaption.
   */
  @Nonnull
  public static <T extends BaseFile> T adaptToFileType(@Nonnull final BaseResource fileResource,
          @Nonnull final Class<T> type) throws InvalidResourceTypeException {
    T file = adaptTo(fileResource, type);
    if (fileResource.getName().endsWith(file.getFileType().getExtension())) {
      if (fileResource.getResource().adaptTo(type) != null) {
        if (file.getFileType().getReadableContentTypes().contains(file.getMimeType())) {
          return file;
        } else {
          throw new InvalidResourceTypeException(fileResource.getPath(), type,
                  String.format("File mimeType '%s' did not match any expected types.",
                          file.getMimeType()));
        }
      } else {
        throw new InvalidResourceTypeException(fileResource.getPath(), type);
      }
    } else {
      throw new InvalidResourceTypeException(file.getPath(), type,
              String.format("File did not have extension `%s`.",
                      file.getFileType().getExtension()));
    }
  }

  /**
   * Adapts a Resource to a specified Model type that extends BaseFile.  Adapted object must pass
   * validation ( no errors ).
   *
   * @param fileResource Resource to adapt.
   * @param type Model type to adapt the resource to. Must extend BaseFile.
   * @param <T> Model type to adapt the resource to. Must extend BaseFile.
   *
   * @return The Resource adapted to the specified FileType.
   * @throws InvalidResourceTypeException Thrown when the Resource cannot be adapted to the
   *         passed type, or has an error after adaption.
   */
  @Nonnull
  public static <T extends BaseFile> T adaptToFileType(@Nonnull final Resource fileResource,
          @Nonnull final Class<T> type) throws InvalidResourceTypeException {
    return adaptToFileType(adaptToBaseResource(fileResource), type);
  }

  /**
   * Retrieves the specified Resource, adapted to the BaseFile implementation type (if possible).
   *
   * @param path Absolute path to the Resource to retrieve.
   * @param resolver ResourceResolver.
   * @param type Model type to adapt the resource to. Must extend BaseFile.
   * @param <T> Extends {@link BaseFile}
   *
   * @return the specified Resource, adapted to the BaseFile implementation type.
   * @throws ResourceNotFoundException No Resource found at the specified path.
   * @throws InvalidResourceTypeException Resource was found at the specified path, but could
   *         not be adapted to the expected BaseFile implementation Model type.
   */
  @Nonnull
  public static <T extends BaseFile> T getResourceAsFileType(@Nonnull final String path,
          @Nonnull final ResourceResolver resolver, @Nonnull final Class<T> type)
          throws ResourceNotFoundException, InvalidResourceTypeException {
    return adaptToFileType(getResourceAsBaseResource(path, resolver), type);
  }

  /**
   * Retrieves a child Resource as the specified BaseFile implementation Model.
   *
   * @param name Name of expected child Resource
   * @param resource Resource to retrieve child of.
   * @param type Sling Model type to adapt to.
   * @param <T> Extends {@link BaseFile}
   *
   * @return The child Resource as the specified BaseFile implementation Model.
   * @throws ChildResourceNotFoundException No child resource matching the specified name was
   *         found.
   * @throws InvalidResourceTypeException The specified child Resource was found, but could not
   *         be adapted to the expected BaseFile implementation Model. Most likely caused by an
   *         invalid
   *         jcr:mimeType value.
   */
  @Nonnull
  public static <T extends BaseFile> T getChildAsFileType(@Nonnull final String name,
          @Nonnull final Resource resource, @Nonnull final Class<T> type)
          throws ChildResourceNotFoundException, InvalidResourceTypeException {
    return adaptToFileType(getChildAsBaseResource(name, resource), type);
  }

  /**
   * Retrieves a child Resource as the specified BaseFile implementation Model.
   *
   * @param name Name of expected child Resource
   * @param baseResource Resource to retrieve child of.
   * @param type Sling Model type to adapt to.
   * @param <T> Extends {@link BaseFile}
   *
   * @return The child Resource as the specified BaseFile implementation Model.
   * @throws ChildResourceNotFoundException No child resource matching the specified name was
   *         found.
   * @throws InvalidResourceTypeException The specified child Resource was found, but could not
   *         be adapted to the expected BaseFile implementation Model. Most likely caused by an
   *         invalid
   *         jcr:mimeType value.
   */
  @Nonnull
  public static <T extends BaseFile> T getChildAsFileType(@Nonnull final String name,
          @Nonnull final BaseResource baseResource, @Nonnull final Class<T> type)
          throws ChildResourceNotFoundException, InvalidResourceTypeException {
    return getChildAsFileType(name, baseResource.getResource(), type);
  }

  /**
   * Retrieves a list of child resources which are of a specified file type.
   *
   * @param resource resource to retrieve children of.
   * @param type FileType
   * @param <T> extends BaseFile
   *
   * @return A list of child resources which are of a specified file type.
   */
  @Nonnull
  public static <T extends BaseFile> List<T> getChildrenOfFileType(@Nonnull final Resource resource,
          @Nonnull final Class<T> type) {
    List<T> children = new ArrayList<>();
    for (Resource child : resource.getChildren()) {
      try {
        children.add(adaptToFileType(child, type));
      } catch (InvalidResourceTypeException e) {
        LOG.trace("Attempted to adapt {} to {} but failed.",
                  child.getPath().replaceAll("[\r\n]", ""),
                  type.getName().replaceAll("[\r\n]", ""));
      }
    }
    return children;
  }

  /**
   * Retrieves a list of child resources which are of a specified file type.
   *
   * @param baseResource resource to retrieve children of.
   * @param type FileType
   * @param <T> extends BaseFile
   *
   * @return A list of child resources which are of a specified file type.
   */
  @Nonnull
  public static <T extends BaseFile> List<T> getChildrenOfFileType(
          @Nonnull final BaseResource baseResource,
          @Nonnull final Class<T> type) {
    return getChildrenOfFileType(baseResource.getResource(), type);
  }

}
