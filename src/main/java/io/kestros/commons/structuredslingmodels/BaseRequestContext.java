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

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

/**
 * Baseline Sling Model for extending Request based Models.
 */
@Model(adaptables = SlingHttpServletRequest.class, resourceType = "sling/servlet/default")
public class BaseRequestContext implements BaseSlingModel {

  /**
   * SlingHttpServletRequest the current Model was adapted from.
   */
  @Self
  private SlingHttpServletRequest request;

  /**
   * SlingHttpServletResponse associated to the current Request.
   */
  @SlingObject
  @Optional
  private SlingHttpServletResponse response;

  /**
   * SlingHttpServletRequest the model was initially adapted from.
   *
   * @return SlingHttpServletRequest the model was initially adapted from.
   * @throws IllegalStateException if request is null.
   */
  @SuppressFBWarnings({"AI_ANNOTATION_ISSUES_NEEDS_NULLABLE", "WEM_WEAK_EXCEPTION_MESSAGING"})
  @Nonnull
  @JsonIgnore
  public SlingHttpServletRequest getRequest() {
    if (request != null) {
      return request;
    }
    throw new IllegalStateException(
            "Request was null, which should not be possible for a Sling Model adapted from a "
                    + "SlingHttpServletRequest.");
  }

  /**
   * SlingHttpServletResponse associated with the current request.
   *
   * @return SlingHttpServletResponse associated with the current request.
   */
  @Nullable
  @JsonIgnore
  public SlingHttpServletResponse getResponse() {
    return response;
  }

  /**
   * ResourceResolver associated to the current request.
   *
   * @return ResourceResolver associated to the current request.
   */
  @Nonnull
  @JsonIgnore
  public ResourceResolver getResourceResolver() {
    return getRequest().getResourceResolver();
  }

  @Nonnull
  @Override
  public Resource getResource() {
    return request.getResource();
  }
}