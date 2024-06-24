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

package io.kestros.commons.structuredslingmodels.exceptions;

import javax.annotation.Nonnull;

/**
 * Generic exception for when a desired Resource cannot be found.
 */
public class ResourceNotFoundException extends ModelAdaptionException {



  /**
   * Generic exception for when a desired Resource cannot be found.
   *
   * @param resourcePath Expected Resource path.
   */
  public ResourceNotFoundException(@Nonnull final String resourcePath) {
    this(resourcePath, "Resource not found.");
  }

  /**
   * Generic exception for when a desired Resource cannot be found.
   *
   * @param resourcePath Expected Resource path.
   * @param message Cause message.
   */
  public ResourceNotFoundException(@Nonnull final String resourcePath,
          @Nonnull final String message) {
    super(resourcePath, message);
  }
}
