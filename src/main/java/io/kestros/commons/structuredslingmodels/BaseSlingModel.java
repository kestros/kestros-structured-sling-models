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

package io.kestros.commons.structuredslingmodels;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kestros.commons.structuredslingmodels.annotation.KestrosModel;
import io.kestros.commons.structuredslingmodels.services.ValidationProviderService;
import io.kestros.commons.structuredslingmodels.validation.ModelValidationService;
import io.kestros.commons.structuredslingmodels.validation.ModelValidator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Baseline class for extending Sling Models off of. Used for validation logic. Duplicate messages
 * are not added. Messages are generally used for communicating Model status to developers and
 * content authors.
 */
public class BaseSlingModel {

  private static final Logger LOG = LoggerFactory.getLogger(BaseSlingModel.class);

  @SuppressWarnings("unused")
  @OSGiService
  @Optional
  private ValidationProviderService validationProviderService;

  private ModelValidationService modelValidationService;

  /**
   * List of error messages. Should be used for issues that should not persist.
   */
  private final List<String> errorMessages = new ArrayList<>();

  /**
   * List of warning messages. Should be used for issues that should be fixed, but are allowed to
   * persist.
   */
  private final List<String> warningMessages = new ArrayList<>();

  /**
   * List of info messages. Should be used for to communicate information back to the user.
   */
  private final List<String> infoMessages = new ArrayList<>();

  /**
   * All error message Strings, as a List.
   *
   * @return All error message Strings, as a List.
   */
  @Nonnull
  @JsonIgnore
  public List<String> getErrorMessages() {
    return errorMessages;
  }

  /**
   * Adds passed String to model's
   * {@link io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType#ERROR}
   * Messages.
   *
   * @param errorMessage Message to append to the current List of errors.
   */
  public void addErrorMessage(@Nonnull final String errorMessage) {
    addMessageToList(errorMessage, errorMessages);
  }

  /**
   * All
   * {@link io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType#WARNING}
   * message Strings, as a List.
   *
   * @return All warning message Strings, as a List.
   */
  @Nonnull
  @JsonIgnore
  public List<String> getWarningMessages() {
    return warningMessages;
  }

  /**
   * Adds passed String to model's
   * {@link io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType#WARNING}
   * Messages.
   *
   * @param warningMessage Message to append to the current List of {@link
   *     io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType#WARNING}
   *     messages.
   */
  public void addWarningMessage(@Nonnull final String warningMessage) {
    addMessageToList(warningMessage, warningMessages);
  }

  /**
   * All {@link io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType#INFO}
   * message Strings, as a List.
   *
   * @return All info message Strings, as a List.
   */
  @Nonnull
  @JsonIgnore
  public List<String> getInfoMessages() {
    return infoMessages;
  }

  /**
   * Adds passed String to model's
   * {@link io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType#INFO}
   * Messages.
   *
   * @param infoMessage Message to append to the current List of {@link
   *     io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType#INFO}
   *     messages.
   */
  public void addInfoMessage(@Nonnull final String infoMessage) {
    addMessageToList(infoMessage, infoMessages);
  }

  /**
   * Generic method for adding a message to a message List.
   *
   * @param message message String to append to List.
   * @param list List to append message to.
   */
  private void addMessageToList(@Nonnull final String message, @Nonnull final List<String> list) {
    if (!list.contains(message)) {
      list.add(message);
    }
  }

  /**
   * Retrieve all Validators for the current Model.
   *
   * @return All Validators for the current Model.
   */
  @JsonIgnore
  @Nonnull
  public List<ModelValidator> getValidators() {
    if (getValidationProviderService() != null) {
      return getValidationProviderService().getValidators(this, getModelValidationService());
    }
    LOG.error("Unable to retrieve ValidationProviderService getting Validators for {}",
        getClass().getSimpleName());
    return Collections.emptyList();
  }

  /**
   * List of all BasicValidators for the current Model.
   *
   * @return List of all BasicValidators for the current Model.
   */
  @JsonIgnore
  @Nonnull
  public List<ModelValidator> getBasicValidators() {
    if (getValidationProviderService() != null) {
      return getValidationProviderService().getBasicValidators(this, getModelValidationService());
    }
    LOG.error("Unable to retrieve ValidationProviderService getting Basic Validators for {}",
        getClass().getSimpleName());
    return Collections.emptyList();
  }

  /**
   * List of all DetailedValidators for the current Model.
   *
   * @return List of all DetailedValidators for the current Model.
   */
  @JsonIgnore
  @Nonnull
  public List<ModelValidator> getDetailedValidators() {
    if (getValidationProviderService() != null) {
      return getValidationProviderService().getDetailedValidators(this,
          getModelValidationService());
    }
    LOG.error("Unable to retrieve ValidationProviderService getting Detailed Validators for {}.",
        getClass().getSimpleName());
    return Collections.emptyList();
  }

  /**
   * Perform basic validation.  This will not include detailed validators to improve performance.
   */
  public void validate() {
    if (getValidationProviderService() != null) {
      getValidationProviderService().doBasicValidation(this, getModelValidationService());
    }
  }

  /**
   * Perform detailed validation.  Runs heavier validation tasks (such as those that reference other
   * Model types) and should be used only when required.
   */
  public void doDetailedValidation() {
    if (getValidationProviderService() != null) {
      getValidationProviderService().doDetailedValidation(this, getModelValidationService());
    }
  }

  @Nullable
  ValidationProviderService getValidationProviderService() {
    return this.validationProviderService;
  }

  @Nullable
  ModelValidationService getModelValidationService() {
    if (modelValidationService == null && getValidationProviderService() != null) {
      modelValidationService = getValidationProviderService().getModelValidationService(this);
    }
    return modelValidationService;
  }

}
