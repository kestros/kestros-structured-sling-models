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

import io.kestros.commons.structuredslingmodels.BaseSlingModel;
import io.kestros.commons.structuredslingmodels.annotation.StructuredModel;
import io.kestros.commons.structuredslingmodels.services.ValidationProviderService;
import io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType;
import io.kestros.commons.structuredslingmodels.validation.ModelValidationService;
import io.kestros.commons.structuredslingmodels.validation.ModelValidator;
import io.kestros.commons.structuredslingmodels.validation.ModelValidatorBundle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Provides uncached Model validation to {@link StructuredModel} instances.
 */
@Component(immediate = true,
           service = ValidationProviderService.class,
           property = "service.ranking:Integer=100")
public class BaseValidationProviderService implements ValidationProviderService {

  private static final Logger LOG = LoggerFactory.getLogger(BaseValidationProviderService.class);


  @Override
  public <T extends BaseSlingModel> List<ModelValidator> getValidators(@Nonnull final T model,
      final ModelValidationService modelValidationService) {
    if (modelValidationService != null) {
      final List<ModelValidator> modelValidators = new ArrayList<>();

      modelValidators.addAll(getBasicValidators(model, modelValidationService));
      modelValidators.addAll(getDetailedValidators(model, modelValidationService));

      return modelValidators;
    }
    return Collections.emptyList();
  }

  @Override
  public <T extends BaseSlingModel> List<ModelValidator> getBasicValidators(@Nonnull final T model,
      final ModelValidationService modelValidationService) {
    if (modelValidationService != null) {
      if (modelValidationService.getBasicValidators().isEmpty()) {
        modelValidationService.setModel(model);
        modelValidationService.registerBasicValidators();
      }
      return modelValidationService.getBasicValidators();
    }
    return Collections.emptyList();
  }

  /**
   * Retrieves detailed ModelValidators for a given Model.  Model must extend {@link
   * BaseSlingModel}.
   *
   * @param model Model to validate.
   * @param modelValidationService ModelValidationService to pull ModelValidators from.
   * @param <T> Extends {@link BaseSlingModel}
   * @return Detailed ModelValidators for a given Model.  Model must extend {@link BaseSlingModel}.
   */
  @Override
  public <T extends BaseSlingModel> List<ModelValidator> getDetailedValidators(@Nonnull final T model,
      final ModelValidationService modelValidationService) {
    if (modelValidationService != null) {
      if (modelValidationService.getDetailedValidators().isEmpty()) {
        modelValidationService.setModel(model);
        modelValidationService.registerDetailedValidators();
      }
      return modelValidationService.getDetailedValidators();
    }
    return Collections.emptyList();
  }

  /**
   * Performs basic validation for a specified Model.
   *
   * @param model Model to validate.
   * @param modelValidationService ModelValidationService to retrieve {@link ModelValidator}
   *     registered as basic from.
   * @param <T> Extends {@link BaseSlingModel}.
   */
  @Override
  public <T extends BaseSlingModel> void doBasicValidation(@Nonnull final T model,
      final ModelValidationService modelValidationService) {
    validateModelValidatorList(model, getBasicValidators(model, modelValidationService));
  }

  /**
   * Performs detailed validation for a specified Model.
   *
   * @param model Model to validate.
   * @param modelValidationService ModelValidationService to retrieve {@link ModelValidator}
   *     registered as basic and detailed from.
   * @param <T> Extends {@link BaseSlingModel}.
   */
  @Override
  public <T extends BaseSlingModel> void doDetailedValidation(@Nonnull final T model,
      final ModelValidationService modelValidationService) {
    validateModelValidatorList(model, getBasicValidators(model, modelValidationService));
    validateModelValidatorList(model, getDetailedValidators(model, modelValidationService));
  }

  /**
   * Validates of list of validators against the current Model. If a validator bundle fails, child
   * validators will not be performed.
   *
   * @param validatorList List of validators to perform validation on.
   */
  private <T extends BaseSlingModel> void validateModelValidatorList(final T model,
      final List<ModelValidator> validatorList) {
    for (final ModelValidator validator : validatorList) {

      if (validator instanceof ModelValidatorBundle) {
        final ModelValidatorBundle validatorBundle = (ModelValidatorBundle) validator;

        if (!validatorBundle.isValid()) {
          addBasicValidatorMessagesToLists(model, validatorBundle);
        }
      } else if (!validator.isValid() && validator.getType() != null) {
        addBasicValidatorMessagesToLists(model, validator);
      }
    }
  }

  @Override
  @Nullable
  public <T extends BaseSlingModel> ModelValidationService getModelValidationService(final T model) {
    final Class<? extends BaseSlingModel> modelClass = model.getClass();
    try {
      if (modelClass.getAnnotation(StructuredModel.class) != null) {
        return modelClass.getAnnotation(StructuredModel.class).validationService().newInstance();
      }
    } catch (final InstantiationException exception) {
      LOG.warn("Unable to instantiate ModelValidationService {} for {}", modelClass.getAnnotation(
          StructuredModel.class).validationService().getSimpleName(), modelClass.getSimpleName());
    } catch (final IllegalAccessException exception) {
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
  private <T extends BaseSlingModel> void addBasicValidatorMessagesToLists(final T model,
      final ModelValidator validator) {
    String message = validator.getMessage();
    final ModelValidationMessageType type = validator.getType();

    if (validator instanceof ModelValidatorBundle) {
      message = ((ModelValidatorBundle) validator).getBundleMessage();
    }

    switch (type) {
      case ERROR:
        model.addErrorMessage(message);
        break;
      case WARNING:
        model.addWarningMessage(message);
        break;
      default:
        model.addInfoMessage(message);
        break;
    }
  }
}
