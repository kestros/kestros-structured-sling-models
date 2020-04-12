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

package io.kestros.commons.structuredslingmodels.services;

import io.kestros.commons.structuredslingmodels.BaseSlingModel;
import io.kestros.commons.structuredslingmodels.validation.ModelValidationService;
import io.kestros.commons.structuredslingmodels.validation.ModelValidator;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Service that providers validation messages to Sling Models with the {@link
 * io.kestros.commons.structuredslingmodels.annotation.StructuredModel} annotation.
 */
public interface ValidationProviderService {

  /**
   * ModelValidationService annotated to the current Sling Model Class.
   *
   * @param model Model to retrieve the associated ModelValidationService of.
   * @param <T> extends {@link BaseSlingModel}.
   * @return ModelValidationService annotated to the current Sling Model Class.
   */
  <T extends BaseSlingModel> ModelValidationService getModelValidationService(T model);

  /**
   * Retrieves all ModelValidators for a given Model.  Model must extend {@link BaseSlingModel}.
   *
   * @param model Model to validate.
   * @param modelValidationService ModelValidationService to pull ModelValidators from.
   * @param <T> Extends {@link BaseSlingModel}
   * @return All ModelValidators for a given Model.  Model must extend {@link BaseSlingModel}.
   */
  <T extends BaseSlingModel> List<ModelValidator> getValidators(@Nonnull T model,
      ModelValidationService modelValidationService);

  /**
   * Retrieves basic ModelValidators for a given Model.  Model must extend {@link BaseSlingModel}.
   *
   * @param model Model to validate.
   * @param modelValidationService ModelValidationService to pull ModelValidators from.
   * @param <T> Extends {@link BaseSlingModel}
   * @return Basic ModelValidators for a given Model.  Model must extend {@link BaseSlingModel}.
   */
  <T extends BaseSlingModel> List<ModelValidator> getBasicValidators(@Nonnull T model,
      ModelValidationService modelValidationService);

  /**
   * Retrieves detailed ModelValidators for a given Model.  Model must extend {@link
   * BaseSlingModel}.
   *
   * @param model Model to validate.
   * @param modelValidationService ModelValidationService to retrieve {@link ModelValidator}
   *     registered as detailed from.
   * @param <T> Extends {@link BaseSlingModel}
   * @return Detailed ModelValidators for a given Model.  Model must extend {@link BaseSlingModel}.
   */
  <T extends BaseSlingModel> List<ModelValidator> getDetailedValidators(@Nonnull T model,
      ModelValidationService modelValidationService);

  /**
   * Performs detailed validation for a specified Model.
   *
   * @param model Model to validate.
   * @param modelValidationService ModelValidationService to retrieve {@link ModelValidator}
   *     registered as basic from.
   * @param <T> Extends {@link BaseSlingModel}.
   */
  <T extends BaseSlingModel> void doBasicValidation(@Nonnull T model,
      ModelValidationService modelValidationService);


  /**
   * Performs detailed validation for a specified Model.
   *
   * @param model Model to validate.
   * @param modelValidationService ModelValidationService to retrieve {@link ModelValidator}
   *     registered as basic and detailed from.
   * @param <T> Extends {@link BaseSlingModel}.
   */
  <T extends BaseSlingModel> void doDetailedValidation(@Nonnull T model,
      ModelValidationService modelValidationService);

}
