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
 * Thrown when no ancestor Resources can be adapted to the passed Sling Model type.
 */
public class NoValidAncestorException extends ModelAdaptionException {


  /**
   * Thrown when no ancestor Resources can be adapted to the passed Sling Model type.
   *
   * @param resourcePath Absolute path of the origin Resource.
   * @param type Model type which no ancestor Resource could be adapted to.
   */
  public NoValidAncestorException(@Nonnull final String resourcePath,
          @Nonnull final Class<?> type) {
    this(resourcePath, type, "No valid ancestor found.");
  }

  /**
   * Thrown when no ancestor Resources can be adapted to the passed Sling Model type.
   *
   * @param resourcePath Absolute path of the origin Resource.
   * @param type Model type which no ancestor Resource could be adapted to.
   * @param cause Cause exception.
   */
  public NoValidAncestorException(@Nonnull final String resourcePath,
          @Nonnull final Class<?> type, @Nonnull final Throwable cause) {
    this(resourcePath, type, "No valid ancestor found.", cause);
  }

  /**
   * Thrown when no ancestor Resources can be adapted to the passed Sling Model type.
   *
   * @param resourcePath Absolute path of the origin Resource.
   * @param type Model type which no ancestor Resource could be adapted to.
   * @param message Cause message.
   */
  public NoValidAncestorException(@Nonnull final String resourcePath, @Nonnull final Class<?> type,
          @Nonnull final String message) {
    super(String.format("Unable to retrieve ancestor matching type %s for %s: %s",
                        type.getSimpleName(), resourcePath, message));
  }

  /**
   * Thrown when no ancestor Resources can be adapted to the passed Sling Model type.
   *
   * @param resourcePath Absolute path of the origin Resource.
   * @param type Model type which no ancestor Resource could be adapted to.
   * @param message Cause message.
   * @param cause Cause exception.
   */
  public NoValidAncestorException(@Nonnull final String resourcePath, @Nonnull final Class<?> type,
          @Nonnull final String message, @Nonnull final Throwable cause) {
    super(String.format("Unable to retrieve ancestor matching type %s for %s: %s",
                        type.getSimpleName(), resourcePath, message), cause);
  }
}
