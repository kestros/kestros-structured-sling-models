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

/**
 * Thrown when a Resource cannot be adapted to a Sling Model class because the sling:resourceType or
 * jcr:primaryType does not match the expected value (based on the `resourceType` value of the
 * Model's `@Model` annotation).
 */
public class InvalidResourceTypeException extends ModelAdaptionException {

  private static final long serialVersionUID = 8669249118588546600L;

  protected InvalidResourceTypeException(String message) {
    super(message);
  }

  protected InvalidResourceTypeException(String resource, String message) {
    super(resource, message);
  }

  public InvalidResourceTypeException(String resource, Class type) {
    super(resource, type, "Invalid resource type.");
  }

  public InvalidResourceTypeException(String resource, Class type, String message) {
    super(resource, type, message);
  }
}
