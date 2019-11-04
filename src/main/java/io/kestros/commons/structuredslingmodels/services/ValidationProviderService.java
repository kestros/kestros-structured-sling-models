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

public interface ValidationProviderService {

  <T extends BaseSlingModel> ModelValidationService getModelValidationService(T model);

  <T extends BaseSlingModel> List<ModelValidator> getValidators(@Nonnull T model,
      ModelValidationService modelValidationService);

  <T extends BaseSlingModel> List<ModelValidator> getBasicValidators(@Nonnull T model,
      ModelValidationService modelValidationService);

  <T extends BaseSlingModel> List<ModelValidator> getDetailedValidators(@Nonnull T model,
      ModelValidationService modelValidationService);

  <T extends BaseSlingModel> void doBasicValidation(@Nonnull T model,
      ModelValidationService modelValidationService);

  <T extends BaseSlingModel> void doDetailedValidation(@Nonnull T model,
      ModelValidationService modelValidationService);

  <T extends BaseSlingModel> void flushCachedValidation(@Nonnull T model);

}
