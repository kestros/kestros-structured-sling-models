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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;

import io.kestros.commons.structuredslingmodels.services.ValidationProviderService;
import io.kestros.commons.structuredslingmodels.services.impl.BaseValidationProviderService;
import io.kestros.commons.structuredslingmodels.utilities.SampleResourceModel;
import io.kestros.commons.structuredslingmodels.validation.DefaultModelValidationService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class BaseSlingModelTest {

  @Rule
  public final SlingContext context = new SlingContext();

  private BaseSlingModel model;
  private BaseValidationProviderService validationProviderService;

  Map<String, Object> properties = new HashMap<>();
  Resource resource;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    validationProviderService = new BaseValidationProviderService();

    context.registerService(ValidationProviderService.class, validationProviderService);

    model = new BaseSlingModel();
    model = spy(model);
    resource = context.create().resource("/resource", properties);
  }

  @Test
  public void testGetErrorMessages() throws Exception {
    model.addErrorMessage("This is an error message.");
    model.addErrorMessage("This is an error message.");
    model.addErrorMessage("This is another error message.");
    model.addErrorMessage("This is another error message.");

    model.addWarningMessage("This is a warning message.");
    model.addWarningMessage("This is a warning message.");
    model.addWarningMessage("This is another warning message.");
    model.addWarningMessage("This is another warning message.");

    model.addInfoMessage("This is an info message.");
    model.addInfoMessage("This is an info message.");
    model.addInfoMessage("This is another info message.");
    model.addInfoMessage("This is another info message.");

    assertEquals(2, model.getErrorMessages().size());
    assertEquals("This is an error message.", model.getErrorMessages().get(0));
    assertEquals("This is another error message.", model.getErrorMessages().get(1));
  }

  @Test
  public void testGetWarningMessages() throws Exception {
    model.addErrorMessage("This is an error message.");
    model.addErrorMessage("This is an error message.");
    model.addErrorMessage("This is another error message.");
    model.addErrorMessage("This is another error message.");

    model.addWarningMessage("This is a warning message.");
    model.addWarningMessage("This is a warning message.");
    model.addWarningMessage("This is another warning message.");
    model.addWarningMessage("This is another warning message.");

    model.addInfoMessage("This is an info message.");
    model.addInfoMessage("This is an info message.");
    model.addInfoMessage("This is another info message.");
    model.addInfoMessage("This is another info message.");

    assertEquals(2, model.getWarningMessages().size());
    assertEquals("This is a warning message.", model.getWarningMessages().get(0));
    assertEquals("This is another warning message.", model.getWarningMessages().get(1));
  }

  @Test
  public void testGetInfoMessages() throws Exception {
    model.addErrorMessage("This is an error message.");
    model.addErrorMessage("This is an error message.");
    model.addErrorMessage("This is another error message.");
    model.addErrorMessage("This is another error message.");

    model.addWarningMessage("This is a warning message.");
    model.addWarningMessage("This is a warning message.");
    model.addWarningMessage("This is another warning message.");
    model.addWarningMessage("This is another warning message.");

    model.addInfoMessage("This is an info message.");
    model.addInfoMessage("This is an info message.");
    model.addInfoMessage("This is another info message.");
    model.addInfoMessage("This is another info message.");

    assertEquals(2, model.getInfoMessages().size());
    assertEquals("This is an info message.", model.getInfoMessages().get(0));
    assertEquals("This is another info message.", model.getInfoMessages().get(1));
  }

  @Test
  public void testGetBasicValidators() {
    model = resource.adaptTo(SampleResourceModel.class);

    assertEquals(10, model.getBasicValidators().size());
  }

  @Test
  public void testGetBasicValidatorsWhenProviderServiceIsNull() {
    model = new SampleResourceModel();

    assertEquals(0, model.getBasicValidators().size());
  }

  @Test
  public void testGetDetailedValidators() {
    model = resource.adaptTo(SampleResourceModel.class);

    assertEquals(1, model.getDetailedValidators().size());
  }

  @Test
  public void testGetDetailedValidatorsWhenProviderServiceIsNull() {
    model = new SampleResourceModel();

    assertEquals(0, model.getDetailedValidators().size());
  }

  @Test
  public void testGetValidators() {
    model = resource.adaptTo(SampleResourceModel.class);

    assertEquals(11, model.getValidators().size());
  }

  @Test
  public void testGetValidatorsWhenProviderServiceIsNull() {
    model = new SampleResourceModel();

    assertEquals(0, model.getValidators().size());
  }

  @Test
  public void testGetValidatorsWhenModelValidationServiceIsNull() {
    model = new BaseResource();
    model = resource.adaptTo(BaseResource.class);

    assertEquals(0, model.getValidators().size());
  }


  @Test
  public void testValidate() {
    model = resource.adaptTo(SampleResourceModel.class);

    model.validate();

    assertEquals(3, model.getErrorMessages().size());
    assertEquals("This is an error validator.", model.getErrorMessages().get(0));
    assertEquals("All must be true bundle. - has false", model.getErrorMessages().get(1));
    assertEquals("One must be true bundle where all validators are false.",
        model.getErrorMessages().get(2));

    assertEquals(1, model.getWarningMessages().size());
    assertEquals("This is a warning validator.", model.getWarningMessages().get(0));

    assertEquals(1, model.getInfoMessages().size());
    assertEquals("This is an info validator.", model.getInfoMessages().get(0));
  }

  @Test
  public void testValidateWithDetailedValidation() {
    model = resource.adaptTo(SampleResourceModel.class);

    model.doDetailedValidation();

    assertEquals(4, model.getErrorMessages().size());
    assertEquals("This is an error validator.", model.getErrorMessages().get(0));
    assertEquals("All must be true bundle. - has false", model.getErrorMessages().get(1));
    assertEquals("One must be true bundle where all validators are false.",
        model.getErrorMessages().get(2));

    assertEquals(1, model.getWarningMessages().size());
    assertEquals("This is a warning validator.", model.getWarningMessages().get(0));

    assertEquals(1, model.getInfoMessages().size());
    assertEquals("This is an info validator.", model.getInfoMessages().get(0));
  }

  //
  @Test
  public void testDoDetailedValidationWhenModelValidationServiceIsNull() {

    model = resource.adaptTo(BaseResource.class);

    model.doDetailedValidation();

    assertEquals(0, model.getErrorMessages().size());

    assertEquals(0, model.getWarningMessages().size());

    assertEquals(0, model.getInfoMessages().size());
  }

  @Test
  public void testGetModelValidationService() {
    model = resource.adaptTo(SampleResourceModel.class);

    assertNotNull(model.getModelValidationService());
    Assert.assertEquals("SampleModelValidationService",
        model.getModelValidationService().getClass().getSimpleName());

  }

  @Test
  public void testGetModelValidationServiceWhenNoneSet() {
    model = resource.adaptTo(BaseResource.class);
    Assert.assertEquals(DefaultModelValidationService.class,
        model.getModelValidationService().getClass());
  }
}