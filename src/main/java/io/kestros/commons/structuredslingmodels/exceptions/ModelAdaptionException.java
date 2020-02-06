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

import org.apache.sling.api.resource.Resource;

/**
 * Generic exception for when an error occurs when attempting to adapt a Resource.
 */
public class ModelAdaptionException extends Exception {

  private static final long serialVersionUID = -4857016290098398418L;

  protected ModelAdaptionException(final String message) {
    super(message);
  }

  /**
   * Generic exception for when an error occurs when attempting to adapt a Resource.
   *
   * @param resource Resource that failed adaption.
   * @param message Cause message.
   */
  public ModelAdaptionException(final Resource resource, final String message) {
    this(resource.getPath(), message);
  }

  /**
   * Generic exception for when an error occurs when attempting to adapt a Resource.
   *
   * @param resourcePath Absolute path to the Resource that failed adaption.
   * @param message Cause message.
   */
  public ModelAdaptionException(final String resourcePath, final String message) {
    super("Unable to adapt '" + resourcePath + "': " + message);
  }

  /**
   * Generic exception for when an error occurs when attempting to adapt a Resource.
   *
   * @param resourcePath Absolute path to the Resource that failed adaption.
   * @param type Model type that the Resource could not be adapted to.
   * @param message Cause message.
   */
  public ModelAdaptionException(final String resourcePath, final Class type, final String message) {
    this("Unable to adapt '" + resourcePath + "' to " + type.getSimpleName() + ": " + message);
  }

}
