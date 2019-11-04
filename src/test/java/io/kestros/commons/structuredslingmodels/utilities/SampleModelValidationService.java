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

package io.kestros.commons.structuredslingmodels.utilities;

import static io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType.ERROR;

import io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType;
import io.kestros.commons.structuredslingmodels.validation.ModelValidationService;
import io.kestros.commons.structuredslingmodels.validation.ModelValidator;
import io.kestros.commons.structuredslingmodels.validation.ModelValidatorBundle;

public class SampleModelValidationService extends ModelValidationService {

  @Override
  public SampleResourceModel getModel() {
    return (SampleResourceModel) getGenericModel();
  }

  @Override
  public void registerBasicValidators() {
    addBasicValidator(getModelValidator(false, "This is an error validator.", ERROR));
    addBasicValidator(getSampleWarningValidator());
    addBasicValidator(getSampleInfoValidator());

    addBasicValidator(getModelValidator(false, "Validator with null message type.", null));

    addBasicValidator(getSampleModelValidatorBundleAllMustBeTrue());
    addBasicValidator(getSampleModelValidatorBundleAllMustBeTrueWhereOneValidatorIsFalse());
    addBasicValidator(getSampleModelValidatorBundleOneMustBeTrue());
    addBasicValidator(getSampleModelValidatorBundleOneMustBeTrueNoneTrue());
    addBasicValidator(getSampleModelValidatorBundleOneMustBeTrueAllTrue());
    addBasicValidator(getSampleIllegalValidator());
  }

  @Override
  public void registerDetailedValidators() {
    addDetailedValidator(getSampleValidatorThatIsNotAlwaysRun());
  }

  private ModelValidator getModelValidator(final boolean isValid, final String message,
      final ModelValidationMessageType type) {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        return isValid;
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

  private ModelValidator getSampleWarningValidator() {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        return false;
      }

      @Override
      public String getMessage() {
        return "This is a warning validator.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return ModelValidationMessageType.WARNING;
      }
    };
  }

  private ModelValidator getSampleInfoValidator() {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        return false;
      }

      @Override
      public String getMessage() {
        return "This is an info validator.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return ModelValidationMessageType.INFO;
      }
    };
  }

  private ModelValidator getSampleIllegalValidator() {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        return false;
      }

      @Override
      public String getMessage() {
        return "This is an info validator.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return null;
      }
    };
  }


  private ModelValidator getSampleValidatorThatIsNotAlwaysRun() {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        return false;
      }

      @Override
      public String getMessage() {
        return "This validator is not always run.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return ERROR;
      }
    };
  }

  private ModelValidatorBundle getSampleModelValidatorBundleAllMustBeTrue() {
    return new ModelValidatorBundle() {

      @Override
      public String getBundleMessage() {
        return "All must be true bundle.";
      }

      @Override
      public void registerValidators() {
        addBasicValidator(getModelValidator(true,
            "This is an error validator that is always true. - all must be " + "true", ERROR));
      }

      @Override
      public boolean isAllMustBeTrue() {
        return true;
      }

      @Override
      public ModelValidationMessageType getType() {
        return ERROR;
      }
    };
  }

  private ModelValidatorBundle getSampleModelValidatorBundleAllMustBeTrueWhereOneValidatorIsFalse() {
    return new ModelValidatorBundle() {

      @Override
      public String getBundleMessage() {
        return "All must be true bundle. - has false";
      }

      @Override
      public void registerValidators() {
        addBasicValidator(
            getModelValidator(false, "This is an error validator - all must be true, one false",
                ERROR));
        addBasicValidator(
            getModelValidator(false, "This is an error validator-2 - all must be true, one false",
                ERROR));
        addBasicValidator(getModelValidator(true,
            "This is an error validator that is always true. - all must be " + "true, one false",
            ERROR));
      }

      @Override
      public boolean isAllMustBeTrue() {
        return true;
      }

      @Override
      public ModelValidationMessageType getType() {
        return ERROR;
      }
    };
  }

  private ModelValidatorBundle getSampleModelValidatorBundleOneMustBeTrue() {
    return new ModelValidatorBundle() {

      @Override
      public String getBundleMessage() {
        return "One must be true bundle.";
      }

      @Override
      public void registerValidators() {
        addBasicValidator(
            getModelValidator(false, "This is an error validator - one must be true.", ERROR));
        addBasicValidator(getModelValidator(true,
            "This is an error validator that is always true. - one must be " + "true, one false",
            ERROR));
      }

      @Override
      public boolean isAllMustBeTrue() {
        return false;
      }

      @Override
      public ModelValidationMessageType getType() {
        return ERROR;
      }
    };
  }

  private ModelValidatorBundle getSampleModelValidatorBundleOneMustBeTrueNoneTrue() {
    return new ModelValidatorBundle() {

      @Override
      public String getBundleMessage() {
        return "One must be true bundle where all validators are false.";
      }

      @Override
      public void registerValidators() {
        addBasicValidator(
            getModelValidator(false, "This is an error validator -  one must be true, is false.",
                ERROR));
      }

      @Override
      public boolean isAllMustBeTrue() {
        return false;
      }

      @Override
      public ModelValidationMessageType getType() {
        return ERROR;
      }
    };
  }

  private ModelValidatorBundle getSampleModelValidatorBundleOneMustBeTrueAllTrue() {
    return new ModelValidatorBundle() {

      @Override
      public String getBundleMessage() {
        return "One must be true bundle where all validators are false.";
      }

      @Override
      public void registerValidators() {
        addBasicValidator(getModelValidator(true,
            "This is an error validator that is always true -  one must be " + "true, is true.",
            ERROR));
      }

      @Override
      public boolean isAllMustBeTrue() {
        return false;
      }

      @Override
      public ModelValidationMessageType getType() {
        return ERROR;
      }
    };
  }
}
