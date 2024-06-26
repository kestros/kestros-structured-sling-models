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
 * Thrown when no parent Resource can be returned.
 */
public class NoParentResourceException extends ModelAdaptionException {


  /**
   * Thrown when no parent Resource can be returned.
   *
   * @param resourcePath Absolute path of Resource that could not retrieve a parent Resource.
   */
  public NoParentResourceException(@Nonnull final String resourcePath) {
    this(resourcePath, "Parent not found.");
  }

  /**
   * Thrown when no parent Resource can be returned.
   *
   * @param resourcePath Absolute path of Resource that could not retrieve a parent Resource.
   * @param message Cause message.
   */
  protected NoParentResourceException(@Nonnull final String resourcePath,
          @Nonnull final String message) {
    super("Unable to retrieve parent of '" + resourcePath + "':" + message);
  }
}