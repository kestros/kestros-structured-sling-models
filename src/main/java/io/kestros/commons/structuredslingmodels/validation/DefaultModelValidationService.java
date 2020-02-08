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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.commons.structuredslingmodels.BaseResource;

/**
 * ModelValidationService to be used if the Model type doesn't have one set.
 */
public class DefaultModelValidationService extends ModelValidationService {

  @SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
  @Override
  public BaseResource getModel() {
    if (getGenericModel() instanceof BaseResource) {
      return (BaseResource) getGenericModel();
    }
    throw new IllegalStateException();
  }

  @Override
  public void registerBasicValidators() {
    // No Basic Validators.
  }

  @Override
  public void registerDetailedValidators() {
    // No Detailed Validators.
  }
}
