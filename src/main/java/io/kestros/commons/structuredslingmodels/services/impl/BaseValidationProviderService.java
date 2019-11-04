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

package io.kestros.commons.structuredslingmodels.services.impl;

import static io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType.ERROR;
import static io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType.INFO;
import static io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType.WARNING;

import io.kestros.commons.structuredslingmodels.BaseSlingModel;
import io.kestros.commons.structuredslingmodels.annotation.StructuredModel;
import io.kestros.commons.structuredslingmodels.services.ValidationProviderService;
import io.kestros.commons.structuredslingmodels.validation.ModelValidationService;
import io.kestros.commons.structuredslingmodels.validation.ModelValidator;
import io.kestros.commons.structuredslingmodels.validation.ModelValidatorBundle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(immediate = true,
           service = ValidationProviderService.class,
           property = "service.ranking:Integer=100")
public class BaseValidationProviderService implements ValidationProviderService {

  private static final Logger LOG = LoggerFactory.getLogger(BaseValidationProviderService.class);

  @Override
  public <T extends BaseSlingModel> List<ModelValidator> getValidators(T model,
      ModelValidationService modelValidationService) {
    if (modelValidationService != null) {
      List<ModelValidator> modelValidators = new ArrayList<>();

      modelValidators.addAll(getBasicValidators(model, modelValidationService));
      modelValidators.addAll(getDetailedValidators(model, modelValidationService));

      return modelValidators;
    }
    return Collections.emptyList();
  }

  @Override
  public <T extends BaseSlingModel> List<ModelValidator> getBasicValidators(T model,
      ModelValidationService modelValidationService) {
    if (modelValidationService != null) {
      if (modelValidationService.getBasicValidators().isEmpty()) {
        modelValidationService.setModel(model);
        modelValidationService.registerBasicValidators();
      }
      return modelValidationService.getBasicValidators();
    }
    return Collections.emptyList();
  }

  @Override
  public <T extends BaseSlingModel> List<ModelValidator> getDetailedValidators(T model,
      ModelValidationService modelValidationService) {
    if (modelValidationService != null) {
      if (modelValidationService.getDetailedValidators().isEmpty()) {
        modelValidationService.setModel(model);
        modelValidationService.registerDetailedValidators();
      }
      return modelValidationService.getDetailedValidators();
    }
    return Collections.emptyList();
  }

  @Override
  public <T extends BaseSlingModel> void doBasicValidation(T model,
      ModelValidationService modelValidationService) {
    validateModelValidatorList(model, getBasicValidators(model, modelValidationService));
  }

  @Override
  public <T extends BaseSlingModel> void doDetailedValidation(T model,
      ModelValidationService modelValidationService) {
    validateModelValidatorList(model, getBasicValidators(model, modelValidationService));
    validateModelValidatorList(model, getDetailedValidators(model, modelValidationService));
  }

  @Override
  public <T extends BaseSlingModel> void flushCachedValidation(T model) {
    // No Cache to flush.
  }


  /**
   * Validates of list of validators against the current Model. If a validator bundle fails, child
   * validators will not be performed.
   *
   * @param validatorList List of validators to perform validation on.
   */
  private <T extends BaseSlingModel> void validateModelValidatorList(T model,
      List<ModelValidator> validatorList) {
    for (ModelValidator validator : validatorList) {

      if (validator instanceof ModelValidatorBundle) {
        ModelValidatorBundle validatorBundle = (ModelValidatorBundle) validator;

        if (!validatorBundle.isValid()) {
          addBasicValidatorMessagesToLists(model, validatorBundle);
        }
      } else if (!validator.isValid() && validator.getType() != null) {
        addBasicValidatorMessagesToLists(model, validator);
      }
    }

  }

  /**
   * ModelValidationService annotated to the current Sling Model Class.
   *
   * @return ModelValidationService annotated to the current Sling Model Class.
   */
  @Nullable
  public <T extends BaseSlingModel> ModelValidationService getModelValidationService(T model) {
    Class<? extends BaseSlingModel> modelClass = model.getClass();
    try {
      if (modelClass.getAnnotation(StructuredModel.class) != null) {
        return modelClass.getAnnotation(StructuredModel.class).validationService().newInstance();
      }
    } catch (InstantiationException exception) {
      LOG.warn("Unable to instantiate ModelValidationService {} for {}", modelClass.getAnnotation(
          StructuredModel.class).validationService().getSimpleName(), modelClass.getSimpleName());
    } catch (IllegalAccessException exception) {
      LOG.warn("Unable to retrieve ModelValidationService {} for {} due to IllegalAccessException",
          modelClass.getAnnotation(StructuredModel.class).validationService().getSimpleName(),
          modelClass.getSimpleName());
    }
    return null;
  }

  /**
   * Performs a Model Validator, and adds the message to the appropriate message list. ( ERROR,
   * WARNING, INFO).
   *
   * @param validator Model validator to be performed.
   */
  private <T extends BaseSlingModel> void addBasicValidatorMessagesToLists(T model,
      ModelValidator validator) {
    String message = validator.getMessage();
    if (validator instanceof ModelValidatorBundle) {
      message = ((ModelValidatorBundle) validator).getBundleMessage();
    }

    if (validator.getType().equals(ERROR)) {
      model.addErrorMessage(message);
    } else if (validator.getType().equals(WARNING)) {
      model.addWarningMessage(message);
    } else if (validator.getType().equals(INFO)) {
      model.addInfoMessage(message);
    }
  }
}
