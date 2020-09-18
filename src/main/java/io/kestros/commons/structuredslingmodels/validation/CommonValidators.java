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

import static io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType.ERROR;
import static io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType.WARNING;

import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.structuredslingmodels.utils.SlingModelUtils;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility Class which holds static validators which are commonly used.
 */
public class CommonValidators {

  /**
   * Validator that checks if the current Resource has a title value.
   *
   * @param model Model to add the validator to.
   * @param <T> Generic model type.
   * @return Validator that checks if the current Resource has a title value.
   */
  public static <T extends BaseResource> ModelValidator hasTitle(final T model) {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        return !model.getName().equals(model.getTitle());
      }

      @Override
      public String getMessage() {
        return "Title is configured.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return ERROR;
      }
    };
  }

  /**
   * Validator that checks if the current Resource has a description value.
   *
   * @param model Model to add the validator to.
   * @param messageType Message type to return failed validation as.
   * @param <T> Generic model type.
   * @return Validator that checks if the current Resource has a description value.
   */
  public static <T extends BaseResource> ModelValidator hasDescription(final T model,
      final ModelValidationMessageType messageType) {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        return StringUtils.isNotEmpty(model.getDescription());
      }

      @Override
      public String getMessage() {
        return "Description is configured.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return messageType;
      }
    };
  }

  /**
   * Validator that checks if the Resource's name ends with the file extension String.
   *
   * @param extension String to match the Resource's name against.
   * @param model Model to add the validator to.
   * @param messageType Message type to return failed validation as.
   * @param <T> Generic model type.
   * @return Validator that checks if the Resource's name ends with the specified String.
   */
  public static <T extends BaseResource> ModelValidator hasFileExtension(final String extension,
      final T model, final ModelValidationMessageType messageType) {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        return model.getName().endsWith(extension);
      }

      @Override
      public String getMessage() {
        return "Resource name ends with " + extension + " extension.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return messageType;
      }
    };
  }

  /**
   * Validator that checks if a specified child Resource exists.
   *
   * @param childName name of the child Resource to check for.
   * @param model Model to add the validator to.
   * @param <T> Generic model type.
   * @return Validator that checks if a specified child Resource exists.
   */
  public static <T extends BaseResource> ModelValidator hasChildResource(final String childName,
      final T model) {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        try {
          SlingModelUtils.getChildAsBaseResource(childName, model);
        } catch (final ChildResourceNotFoundException exception) {
          return false;
        }
        return true;
      }

      @Override
      public String getMessage() {
        return String.format("Has child resource '%s'.", childName);
      }

      @Override
      public ModelValidationMessageType getType() {
        return ERROR;
      }
    };
  }

  /**
   * Validator that checks if a specified child does not fail any ERROR type validators.
   *
   * @param childName name of the child Resource to check for.
   * @param childType type of Model to attempt to adapt the child to. Must extend BaseResource.
   * @param model Model to add the validator to.
   * @param <T> Generic model type.
   * @param <S> Generic model type.
   * @return Validator that checks if a specified child does not fail any ERROR type validators.
   */
  public static <T extends BaseResource, S extends BaseResource>
      ModelValidator isChildResourceValidResourceType(final String childName,
      final Class<S> childType, final T model) {

    return new ModelValidator() {
      @Override
      public boolean isValid() {
        try {
          SlingModelUtils.getChildAsType(childName, model, childType);
        } catch (final ModelAdaptionException exception) {
          return false;
        }
        return true;
      }

      @Override
      public String getMessage() {
        return String.format("Has valid child resource '%s'.", childName);
      }

      @Override
      public ModelValidationMessageType getType() {
        return ERROR;
      }
    };
  }

  /**
   * Model Validator Bundle that checks if a specified child resource exists, and is valid.
   *
   * @param childName name of the child Resource to check for.
   * @param childType type of Model to attempt to adapt the child to. Must extend BaseResource.
   * @param model Model to add the validator to.
   * @param <T> Generic model type.
   * @param <S> Generic model type.
   * @return Model Validator Bundle that checks if a specified child resource exists, and is valid.
   */
  public static <T extends BaseResource, S extends BaseResource> ModelValidatorBundle hasValidChild(
      final String childName, final Class<S> childType, final T model) {
    return new ModelValidatorBundle() {
      @Override
      public String getBundleMessage() {
        return "Has valid child " + childType.getSimpleName() + " '" + childName + "'";
      }

      @Override
      public void registerValidators() {
        addBasicValidator(hasChildResource(childName, model));
        addBasicValidator(isChildResourceValidResourceType(childName, childType, model));
      }

      @Override
      public ModelValidationMessageType getType() {
        return ERROR;
      }

      @Override
      public boolean isAllMustBeTrue() {
        return true;
      }
    };
  }

  /**
   * Builds a List of failed Error validators from a model.
   *
   * @param model model to build Validator List from.
   * @param <T> Generic type
   * @return List of failed Error validators from a model.
   */
  public static <T extends BaseResource> List<ModelValidator> getFailedErrorValidators(
      @Nonnull final T model) {
    final List<ModelValidator> errorValidators = new ArrayList<>();
    for (final String errorMessage : model.getErrorMessages()) {
      final ModelValidator validator = new ModelValidator() {
        @Override
        public boolean isValid() {
          return false;
        }

        @Override
        public String getMessage() {
          return "Error validator failed for " + model.getPath() + ": " + errorMessage;
        }

        @Override
        public ModelValidationMessageType getType() {
          return ERROR;
        }
      };
      errorValidators.add(validator);
    }
    return errorValidators;
  }

  /**
   * Builds a List of failed Warning validators from a model.
   *
   * @param model model to build Validator List from.
   * @param <T> Generic type
   * @return List of failed Error Warning from a model.
   */
  public static <T extends BaseResource> List<ModelValidator> getFailedWarningValidators(
      final T model) {
    final List<ModelValidator> warningValidators = new ArrayList<>();
    for (final String warningMessage : model.getWarningMessages()) {
      final ModelValidator validator = new ModelValidator() {
        @Override
        public boolean isValid() {
          return false;
        }

        @Override
        public String getMessage() {
          return "Warning validator failed for " + model.getPath() + ": " + warningMessage;
        }

        @Override
        public ModelValidationMessageType getType() {
          return WARNING;
        }
      };
      warningValidators.add(validator);
    }
    return warningValidators;
  }

  /**
   * Checks a list for null values.
   *
   * @param list List to check.
   * @param message Validation message.
   * @param type Validation error level.
   * @param <T> Generic type.
   * @return ModelValidator for null values in a specified list.
   */
  public static <T> ModelValidator listContainsNoNulls(@Nonnull final List<T> list,
      @Nonnull final String message, @Nonnull final ModelValidationMessageType type) {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        for (final Object object : list) {
          if (object == null) {
            return false;
          }
        }
        return true;
      }

      @Override
      public String getMessage() {
        return message;
      }

      @Override
      public ModelValidationMessageType getType() {
        return type;
      }
    };
  }

  /**
   * Validates whether a specified list of models has any error messages.
   *
   * @param modelList models to check for errors.
   * @param message Validation message
   * @param <T> Extends base Resource.
   * @return Validates whether a specified list of models has any error messages.
   */
  public static <T extends BaseResource> ModelValidator modelListHasNoErrors(List<T> modelList,
      String message) {
    return modelListHasNoFailedValidatorsOfType(modelList, message, ERROR);
  }

  /**
   * Validates whether a specified list of models has any warning messages.
   *
   * @param modelList models to check for warnings.
   * @param message Validation message
   * @param <T> Extends base Resource.
   * @return Validates whether a specified list of models has any warning messages.
   */
  public static <T extends BaseResource> ModelValidator modelListHasNoWarnings(List<T> modelList,
      String message) {
    return modelListHasNoFailedValidatorsOfType(modelList, message, WARNING);
  }

  private static <T extends BaseResource> ModelValidator modelListHasNoFailedValidatorsOfType(
      List<T> modelList, String message, ModelValidationMessageType type) {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        for (T model : modelList) {
          model.validate();
          if (ERROR.equals(type)) {
            if (!model.getErrorMessages().isEmpty()) {
              return false;
            }
          } else if (WARNING.equals(type) && !model.getWarningMessages().isEmpty()) {
            return false;
          }
        }
        return true;
      }

      @Override
      public String getMessage() {
        return message;
      }

      @Override
      public ModelValidationMessageType getType() {
        return type;
      }
    };
  }

}