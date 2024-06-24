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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javax.annotation.Nonnull;
import org.apache.sling.api.resource.Resource;

/**
 * Generic exception for when an error occurs when attempting to adapt a Resource.
 */
public class ModelAdaptionException extends Exception {


  /**
   * Generic exception for when an error occurs when attempting to adapt a Resource.
   *
   * @param message Cause message.
   */
  public ModelAdaptionException(@Nonnull final String message) {
    super(message);
  }

  /**
   * Generic exception for when an error occurs when attempting to adapt a Resource.
   *
   * @param message Cause message.
   * @param cause Cause exception.
   */
  public ModelAdaptionException(@Nonnull final String message, @Nonnull final Throwable cause) {
    super(message, cause);
  }

  /**
   * Generic exception for when an error occurs when attempting to adapt a Resource.
   *
   * @param resource Resource that failed adaption.
   * @param message Cause message.
   */
  public ModelAdaptionException(@Nonnull final Resource resource, @Nonnull final String message) {
    this(resource.getPath(), message);
  }


  /**
   * Generic exception for when an error occurs when attempting to adapt a Resource.
   *
   * @param resource Resource that failed adaption.
   * @param message Cause message.
   * @param cause Cause exception.
   */
  public ModelAdaptionException(@Nonnull final Resource resource, @Nonnull final String message,
          @Nonnull final Throwable cause) {
    this(resource.getPath(), message, cause);
  }

  /**
   * Generic exception for when an error occurs when attempting to adapt a Resource.
   *
   * @param resourcePath Absolute path to the Resource that failed adaption.
   * @param message Cause message.
   */
  @SuppressFBWarnings("OPM_OVERLY_PERMISSIVE_METHOD")
  public ModelAdaptionException(@Nonnull final String resourcePath, @Nonnull final String message) {
    super("Unable to adapt '" + resourcePath + "': " + message);
  }

  /**
   * Generic exception for when an error occurs when attempting to adapt a Resource.
   *
   * @param resourcePath Absolute path to the Resource that failed adaption.
   * @param message Cause message.
   * @param cause Cause exception.
   */
  @SuppressFBWarnings("OPM_OVERLY_PERMISSIVE_METHOD")
  public ModelAdaptionException(@Nonnull final String resourcePath, @Nonnull final String message,
          @Nonnull final Throwable cause) {
    super("Unable to adapt '" + resourcePath + "': " + message, cause);
  }

  /**
   * Generic exception for when an error occurs when attempting to adapt a Resource.
   *
   * @param resourcePath Absolute path to the Resource that failed adaption.
   * @param type Model type that the Resource could not be adapted to.
   * @param message Cause message.
   */
  @SuppressFBWarnings("OPM_OVERLY_PERMISSIVE_METHOD")
  public ModelAdaptionException(@Nonnull final String resourcePath,
          @Nonnull final Class<?> type, @Nonnull final String message) {
    this("Unable to adapt '" + resourcePath + "' to " + type.getSimpleName() + ": " + message);
  }


  /**
   * Generic exception for when an error occurs when attempting to adapt a Resource.
   *
   * @param resourcePath Absolute path to the Resource that failed adaption.
   * @param type Model type that the Resource could not be adapted to.
   * @param message Cause message.
   * @param cause Cause exception.
   */
  @SuppressFBWarnings("OPM_OVERLY_PERMISSIVE_METHOD")
  public ModelAdaptionException(@Nonnull final String resourcePath,
          @Nonnull final Class<?> type, @Nonnull final String message,
          @Nonnull final Throwable cause) {
    this("Unable to adapt '" + resourcePath + "' to " + type.getSimpleName() + ": " + message,
         cause);
  }

}
