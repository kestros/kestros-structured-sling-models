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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import io.kestros.commons.structuredslingmodels.services.ValidationProviderService;
import io.kestros.commons.structuredslingmodels.services.impl.BaseValidationProviderService;
import io.kestros.commons.structuredslingmodels.utilities.SampleResourceModel;
import io.kestros.commons.structuredslingmodels.validation.DefaultModelValidationService;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class BaseSlingModelTest {

  @Rule
  public final SlingContext context = new SlingContext();

  Map<String, Object> properties = new HashMap<>();
  Resource resource;

  @Before
  public void setUp() {
    context.addModelsForPackage("io.kestros");
    BaseValidationProviderService validationProviderService = new BaseValidationProviderService();

    context.registerService(ValidationProviderService.class, validationProviderService);

    resource = context.create().resource("/resource", properties);
  }

  @Test
  public void testGetErrorMessages() {
    BaseSlingModel model = new BaseSlingModel();

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
  public void testGetWarningMessages() {
    BaseSlingModel model = new BaseSlingModel();

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
  public void testGetInfoMessages() {
    BaseSlingModel model = new BaseSlingModel();

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
    BaseSlingModel model = resource.adaptTo(SampleResourceModel.class);

    assertEquals(10, Objects.requireNonNull(model).getBasicValidators().size());
  }

  @Test
  public void testGetBasicValidatorsWhenProviderServiceIsNull() {
    SampleResourceModel model = new SampleResourceModel();
    model = spy(model);

    assertEquals(0, model.getBasicValidators().size());
  }

  @Test
  public void testGetDetailedValidators() {
    BaseSlingModel model = resource.adaptTo(SampleResourceModel.class);

    assertEquals(1, Objects.requireNonNull(model).getDetailedValidators().size());
  }

  @Test
  public void testGetDetailedValidatorsWhenProviderServiceIsNull() {
    BaseSlingModel model = new BaseSlingModel();
    model = spy(model);

    assertEquals(0, model.getDetailedValidators().size());
  }

  @Test
  public void testGetValidators() {
    SampleResourceModel model = resource.adaptTo(SampleResourceModel.class);

    assertEquals(11, Objects.requireNonNull(model).getValidators().size());
  }

  @Test
  public void testGetValidatorsWhenProviderServiceIsNull() {
    BaseSlingModel model = new BaseSlingModel();
    model = spy(model);

    assertEquals(0, model.getValidators().size());
  }

  @Test
  public void testGetValidatorsWhenModelValidationServiceIsNull() {
    BaseSlingModel model = new BaseSlingModel();

    model = spy(model);

    assertEquals(0, model.getValidators().size());
  }

  @Test
  public void testValidate() {
    BaseSlingModel model = resource.adaptTo(SampleResourceModel.class);

    Objects.requireNonNull(model).validate();

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
  public void testValidateWhenValidationProviderServiceIsNull() {
    BaseSlingModel model = spy(new BaseSlingModel());

    doReturn(null).when(model).getValidationProviderService();

    model.validate();

    assertEquals(0, model.getErrorMessages().size());
    assertEquals(0, model.getWarningMessages().size());
    assertEquals(0, model.getInfoMessages().size());
  }

  @Test
  public void testValidateWithDetailedValidation() {
    BaseSlingModel model = resource.adaptTo(SampleResourceModel.class);

    Objects.requireNonNull(model).doDetailedValidation();

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

    BaseSlingModel model = resource.adaptTo(BaseResource.class);

    Objects.requireNonNull(model).doDetailedValidation();

    assertEquals(0, model.getErrorMessages().size());

    assertEquals(0, model.getWarningMessages().size());

    assertEquals(0, model.getInfoMessages().size());
  }

  @Test
  public void testDoDetailedValidationWhenValidatoinProviderServiceIsNull() {

    BaseSlingModel model = spy(Objects.requireNonNull(resource.adaptTo(BaseResource.class)));

    doReturn(null).when(model).getValidationProviderService();

    model.doDetailedValidation();

    assertEquals(0, model.getErrorMessages().size());
    assertEquals(0, model.getWarningMessages().size());
    assertEquals(0, model.getInfoMessages().size());
  }

  @Test
  public void testGetModelValidationService() {
    BaseSlingModel model = resource.adaptTo(SampleResourceModel.class);

    assertNotNull(Objects.requireNonNull(model).getModelValidationService());
    Assert.assertEquals("SampleModelValidationService",
        Objects.requireNonNull(model.getModelValidationService()).getClass().getSimpleName());

  }

  @Test
  public void testGetModelValidationServiceWhenNoneSet() {
    BaseSlingModel model = resource.adaptTo(BaseResource.class);
    Assert.assertEquals(DefaultModelValidationService.class,
        Objects.requireNonNull(Objects.requireNonNull(model).getModelValidationService()).getClass());
  }

  @Test
  public void testGetModelValidationServiceWhenValidationProviderServiceIsNull() {
    BaseSlingModel model = spy(Objects.requireNonNull(resource.adaptTo(BaseResource.class)));

    doReturn(null).when(model).getValidationProviderService();

    Assert.assertNull(model.getModelValidationService());
  }
}