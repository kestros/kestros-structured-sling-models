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

import io.kestros.commons.structuredslingmodels.BaseSlingModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Baseline ModelValidationService to be extended from.
 */
public abstract class ModelValidationService {

  private BaseSlingModel model;

  private final List<ModelValidator> basicValidators = new ArrayList<>();
  private final List<ModelValidator> detailedValidators = new ArrayList<>();

  /**
   * Sets the model that the ValidationService validates against.
   *
   * @param model model to validate against.
   * @param <T> Model type which extends BaseSlingModel.
   */
  public <T extends BaseSlingModel> void setModel(T model) {
    this.model = model;
  }

  /**
   * Retrieves the model that the ValidationService validates against.
   *
   * @param <T> Model type which extends BaseSlingModel.
   * @return The model that the ValidationService validates against.
   */
  public abstract <T extends BaseSlingModel> T getModel();

  /**
   * Registers ModelValidators.
   */
  public abstract void registerBasicValidators();

  /**
   * Registers ModelValidators.
   */
  public abstract void registerDetailedValidators();

  /**
   * The model to validate against, as a generic BaseSlingModel. Should be casted to a specific
   * Model type.
   *
   * @return The model to validate against, as a generic BaseSlingModel.
   */
  public BaseSlingModel getGenericModel() {
    return model;
  }

  /**
   * Add ModelValidator to the current service, to be checked when the model is adapted, or when
   * detailed validation is called on the model.
   *
   * @param validator Validator to be added to the ValidationService.
   */
  public void addBasicValidator(ModelValidator validator) {
    basicValidators.add(validator);
  }

  public void addDetailedValidator(ModelValidator validator) {
    detailedValidators.add(validator);
  }

  // TODO add referenced validator

  /**
   * List of ModelValidators that always run during PostConstruct.
   *
   * @return List of ModelValidators that always run during PostConstruct.
   */
  public List<ModelValidator> getBasicValidators() {
    return basicValidators;
  }

  /**
   * List of ModelValidators that must be explicitly invoked via doDetailedValidation to run.
   *
   * @return List of ModelValidators that must be explicitly invoked via doDetailedValidation to
   *     run.
   */
  public List<ModelValidator> getDetailedValidators() {
    return detailedValidators;
  }

}
