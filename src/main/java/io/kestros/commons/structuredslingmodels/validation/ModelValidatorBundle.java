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

import java.util.ArrayList;
import java.util.List;

/**
 * ModelValidator that holds a set of ModelValidators.
 */
public abstract class ModelValidatorBundle implements ModelValidator {

  private final List<ModelValidator> validators = new ArrayList<>();

  /**
   * Constructs ModelValidator that holds a set of ModelValidators.
   */
  public ModelValidatorBundle() {
    registerValidators();
  }

  /**
   * Message informing the user what is being validated.  For ModelValidatorBundles, it is either
   * all must be true, or one must be true.
   *
   * @return Message informing the user what is being validated.
   */
  public String getMessage() {
    if (this.isAllMustBeTrue()) {
      return "All of the following are true:";
    } else {
      return "One of the following is true:";
    }
  }

  /**
   * Message informing the user what is being validated within the bundle.
   *
   * @return Message informing the user what is being validated within the bundle.
   */
  public abstract String getBundleMessage();

  /**
   * Registers ModelValidators.
   */
  public abstract void registerValidators();

  /**
   * Whether ModelValidators assigned to the bundle must be true to be considered valid, or just
   * one.
   *
   * @return Whether ModelValidators assigned to the bundle must be true to be considered valid, or
   *     just one.
   */
  public abstract boolean isAllMustBeTrue();

  /**
   * Adds a validator to the bundle.
   *
   * @param validator ModelValidator to add to the bundle.
   */
  public void addBasicValidator(final ModelValidator validator) {
    validators.add(validator);
  }

  /**
   * Adds a list of ModelValidators to the bundle.
   *
   * @param validatorList ModelValidator List to add to the bundle.
   */
  public void addAllValidators(final List<ModelValidator> validatorList) {
    validators.addAll(validatorList);
  }

  /**
   * Whether the current ModelValidatorBundle passes all validators or not.
   *
   * @return Whether the bundle is valid or not.
   */
  public boolean isValid() {
    if (validators.isEmpty()) {
      registerValidators();
    }
    for (final ModelValidator validator : getValidators()) {
      if (isAllMustBeTrue() && !validator.isValid()) {
        return false;
      } else if (!isAllMustBeTrue() && validator.isValid()) {
        return true;
      }
    }
    return isAllMustBeTrue();
  }

  /**
   * List of all ModelsValidators in the bundle.
   *
   * @return List of all ModelsValidators in the bundle.
   */
  public List<ModelValidator> getValidators() {
    return validators;
  }
}