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
 * Exception thrown when no matching Sling Model type can be found for a resource while using {@link
 * org.apache.sling.models.factory.ModelFactory} to
 * {@link org.apache.sling.models.factory.ModelFactory#getModelFromResource(Resource)}.
 */
public class MatchingResourceTypeNotFoundException extends InvalidResourceTypeException {

  private static final long serialVersionUID = 6639023707837948167L;

  /**
   * Exception thrown when no matching Sling Model type can be found for a resource while using
   * {@link org.apache.sling.models.factory.ModelFactory} to
   * {@link org.apache.sling.models.factory.ModelFactory#getModelFromResource(Resource)}.
   *
   * @param resourcePath Absolute path to the Resource that could not be matched to a Sling
   *     Model type.
   */
  public MatchingResourceTypeNotFoundException(final String resourcePath) {
    this(resourcePath, "No matching Sling Model type found.");
  }

  /**
   * Exception thrown when no matching Sling Model type can be found for a resource while using
   * {@link org.apache.sling.models.factory.ModelFactory} to
   * {@link org.apache.sling.models.factory.ModelFactory#getModelFromResource(Resource)}.
   *
   * @param resourcePath Absolute path to the Resource that could not be matched to a Sling
   *     Model type.
   * @param message Cause message.
   */
  public MatchingResourceTypeNotFoundException(final String resourcePath, final String message) {
    super("Unable to retrieve model for " + resourcePath + ": " + message);
  }
}
