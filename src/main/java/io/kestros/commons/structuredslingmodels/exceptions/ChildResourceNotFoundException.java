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

/**
 * Thrown when trying to retrieve a Child of a BaseResource that does not exist or cannot be found
 * (possibly due to permissions).
 */
public class ChildResourceNotFoundException extends ModelAdaptionException {

  /**
   * Thrown when trying to retrieve a Child of a BaseResource that does not exist or cannot be found
   * (possibly due to permissions).
   *
   * @param message Cause message.
   */
  protected ChildResourceNotFoundException(@Nonnull final String message) {
    super(message);
  }

  /**
   * Thrown when trying to retrieve a Child of a BaseResource that does not exist or cannot be found
   * (possibly due to permissions).
   *
   * @param childName Name of expected child Resource.
   * @param parentPath Path of Resource attempting to retrieve child from.
   */
  public ChildResourceNotFoundException(@Nonnull final String childName,
          @Nonnull final String parentPath) {
    this(childName, parentPath, "Child not found.");
  }

  /**
   * Thrown when trying to retrieve a Child of a BaseResource that does not exist or cannot be found
   * (possibly due to permissions).
   *
   * @param childName Name of expected child Resource.
   * @param parentPath Path of Resource attempting to retrieve child from.
   * @param message Cause message.
   */
  @SuppressFBWarnings("OPM_OVERLY_PERMISSIVE_METHOD")
  public ChildResourceNotFoundException(@Nonnull final String childName,
          @Nonnull final String parentPath,
          @Nonnull final String message) {
    super(String.format("Unable to adapt '%s' under '%s': %s", childName, parentPath, message));
  }
}
