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

package io.kestros.commons.structuredslingmodels.validation;

/**
 * Base interface for Model Validation rules.
 */
public interface ModelValidator {

  /**
   * Boolean logic to determine whether the current validator passes validation.
   *
   * @return Whether the current Validator is valid or not.
   */
  boolean isValid();

  /**
   * Message to be shown when the current validator is not valid.
   *
   * @return Message to be shown when the current validator is not valid.
   */
  String getMessage();

  /**
   * The error level of the current validator.  Can be ERROR, WARNING or INFO.
   *
   * @return The error level of the current validator.
   */
  ModelValidationMessageType getType();
}
