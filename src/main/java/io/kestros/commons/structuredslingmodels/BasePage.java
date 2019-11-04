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

package io.kestros.commons.structuredslingmodels;

import static org.apache.jackrabbit.JcrConstants.JCR_CONTENT;

import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.structuredslingmodels.utils.SlingModelUtils;
import javax.annotation.Nonnull;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base Sling Model for any page type.  Dynamically handles jcr:content resources, to allow method
 * calls to be consistent whether the Model was adapted from the root or jcr:content resource.
 */
@Model(adaptables = Resource.class)
public class BasePage extends BaseResource {

  private static final Logger LOG = LoggerFactory.getLogger(BasePage.class);

  /**
   * Returns the root page resource.  If the model was adapted from a jcr:content resource, will
   * return the parent Resource.
   *
   * @return The root Resource associated to the current model.
   */
  @Override
  @Nonnull
  public Resource getResource() {
    Resource adaptedResource = super.getResource();
    Resource adaptedResourceParent = adaptedResource.getParent();
    if (JCR_CONTENT.equals(adaptedResource.getName()) && adaptedResourceParent != null) {
      return adaptedResourceParent;
    }
    return adaptedResource;
  }

  /**
   * Returns the root page Resource name.  If the model was adapted from a jcr:content resource,
   * will return the parent Resource name.
   *
   * @return Name of the current page Resource.
   */
  @Override
  @Nonnull
  public String getName() {
    return getResource().getName();
  }

  /**
   * Returns the root page Resource path.  If the model was adapted from a jcr:content resource,
   * will return the parent Resource path.
   *
   * @return Path of the current page Resource.
   */
  @Override
  @Nonnull
  public String getPath() {
    return getResource().getPath();
  }

  /**
   * Properties of the current Page.  If jcr:content exists as a child, will return the ValueMap of
   * the jcr:content.  To retrieve the properties of the Page resource, use
   * `getResource().getProperties()`.
   *
   * @return Properties ValueMap for the current Page.  If the page has a jcr:content child
   *     Resource, the value of that Resource will be returned.
   */
  @Override
  @Nonnull
  public ValueMap getProperties() {
    try {
      return SlingModelUtils.getChildAsType(JCR_CONTENT, getResource(),
          BaseResource.class).getProperties();
    } catch (ModelAdaptionException exception) {
      LOG.debug(
          "Unable to get jcr:content Resource for {} while getting properties for as BasePage. "
          + "Returning root properties.", getPath());
    }

    return getResource().getValueMap();
  }
}
