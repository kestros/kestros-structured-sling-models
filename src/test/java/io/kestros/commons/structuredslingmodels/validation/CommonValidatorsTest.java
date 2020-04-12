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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.services.ValidationProviderService;
import io.kestros.commons.structuredslingmodels.services.impl.BaseValidationProviderService;
import io.kestros.commons.structuredslingmodels.utilities.SampleFile;
import io.kestros.commons.structuredslingmodels.utilities.SampleResourceModel;
import io.kestros.commons.structuredslingmodels.utils.SlingModelUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class CommonValidatorsTest {

  @Rule
  public SlingContext context = new SlingContext();

  private BaseResource baseResource;
  private Resource resource;
  private Map<String, Object> properties = new HashMap<>();
  private ValidationProviderService validationProviderService = new BaseValidationProviderService();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    context.registerService(ValidationProviderService.class, validationProviderService);
  }


  @Test
  public void testHasTitle() {
    properties.put("jcr:title", "Title");
    resource = context.create().resource("/resource", properties);

    baseResource = resource.adaptTo(BaseResource.class);

    assertEquals("resource", baseResource.getName());
    assertEquals("Title", baseResource.getTitle());

    assertTrue(CommonValidators.hasTitle(baseResource).isValid());
    assertEquals("Title is configured.", CommonValidators.hasTitle(baseResource).getMessage());
    Assert.assertEquals(ModelValidationMessageType.ERROR,
        CommonValidators.hasTitle(baseResource).getType());
  }

  @Test
  public void testHasTitleWhenTitleEqualsName() throws Exception {
    resource = context.create().resource("/resource", properties);

    baseResource = resource.adaptTo(BaseResource.class);

    assertEquals("resource", baseResource.getName());
    assertEquals("resource", baseResource.getTitle());

    assertFalse(CommonValidators.hasTitle(baseResource).isValid());
    assertEquals("Title is configured.", CommonValidators.hasTitle(baseResource).getMessage());
    Assert.assertEquals(ModelValidationMessageType.ERROR,
        CommonValidators.hasTitle(baseResource).getType());
  }

  @Test
  public void testHasDescription() throws Exception {
    properties.put("jcr:description", "Description");
    resource = context.create().resource("/resource", properties);

    baseResource = resource.adaptTo(BaseResource.class);

    assertEquals("Description", baseResource.getDescription());
    assertTrue(
        CommonValidators.hasDescription(baseResource, ModelValidationMessageType.ERROR).isValid());
    assertEquals("Description is configured.", CommonValidators.hasDescription(baseResource,
        ModelValidationMessageType.ERROR).getMessage());
    Assert.assertEquals(ModelValidationMessageType.ERROR,
        CommonValidators.hasDescription(baseResource, ModelValidationMessageType.ERROR).getType());
  }

  @Test
  public void testHasDescriptionWhenFalse() throws Exception {
    resource = context.create().resource("/resource", properties);

    baseResource = resource.adaptTo(BaseResource.class);

    assertEquals("", baseResource.getDescription());
    assertFalse(
        CommonValidators.hasDescription(baseResource, ModelValidationMessageType.ERROR).isValid());
    assertEquals("Description is configured.", CommonValidators.hasDescription(baseResource,
        ModelValidationMessageType.ERROR).getMessage());
    Assert.assertEquals(ModelValidationMessageType.ERROR,
        CommonValidators.hasDescription(baseResource, ModelValidationMessageType.ERROR).getType());
  }

  @Test
  public void testIsFileType() {
    resource = context.create().resource("/resource.css", properties);

    baseResource = resource.adaptTo(BaseResource.class);

    assertTrue(CommonValidators.hasFileExtension("css", baseResource,
        ModelValidationMessageType.ERROR).isValid());
    assertEquals("Resource name ends with css extension.",
        CommonValidators.hasFileExtension("css", baseResource,
            ModelValidationMessageType.ERROR).getMessage());
    Assert.assertEquals(ModelValidationMessageType.ERROR,
        CommonValidators.hasFileExtension("css", baseResource,
            ModelValidationMessageType.ERROR).getType());
  }

  @Test
  public void testIsFileTypeWhenInvalid() {
    resource = context.create().resource("/resource", properties);

    baseResource = resource.adaptTo(BaseResource.class);

    assertFalse(CommonValidators.hasFileExtension("css", baseResource,
        ModelValidationMessageType.ERROR).isValid());
    assertEquals("Resource name ends with css extension.",
        CommonValidators.hasFileExtension("css", baseResource,
            ModelValidationMessageType.ERROR).getMessage());
    Assert.assertEquals(ModelValidationMessageType.ERROR,
        CommonValidators.hasFileExtension("css", baseResource,
            ModelValidationMessageType.ERROR).getType());
  }

  @Test
  public void testHasValidChildResource() {
    resource = context.create().resource("/resource", properties);
    context.create().resource("/resource/child", properties);

    baseResource = resource.adaptTo(BaseResource.class);

    assertTrue(CommonValidators.hasChildResource("child", baseResource).isValid());
    assertEquals("Has child resource 'child'.",
        CommonValidators.hasChildResource("child", baseResource).getMessage());
    Assert.assertEquals(ModelValidationMessageType.ERROR,
        CommonValidators.hasChildResource("child", baseResource).getType());

    assertTrue(CommonValidators.isChildResourceValidResourceType("child", BaseResource.class,
        baseResource).isValid());
    assertEquals("Has valid child resource 'child'.",
        CommonValidators.isChildResourceValidResourceType("child", SampleResourceModel.class,
            baseResource).getMessage());
    Assert.assertEquals(ModelValidationMessageType.ERROR,
        CommonValidators.isChildResourceValidResourceType("child", SampleResourceModel.class,
            baseResource).getType());

    assertTrue(CommonValidators.hasValidChild("child", BaseResource.class, baseResource).isValid());
    assertFalse(
        CommonValidators.hasValidChild("child", SampleResourceModel.class, baseResource).isValid());
    assertEquals("All of the following are true:",
        CommonValidators.hasValidChild("child", SampleResourceModel.class,
            baseResource).getMessage());
    Assert.assertEquals(ModelValidationMessageType.ERROR,
        CommonValidators.hasValidChild("child", SampleResourceModel.class, baseResource).getType());
    assertEquals("Has valid child SampleResourceModel 'child'",
        CommonValidators.hasValidChild("child", SampleResourceModel.class,
            baseResource).getBundleMessage());
  }

  @Test
  public void testHasValidChildResourceWhenInvalid() {
    resource = context.create().resource("/resource", properties);
    context.create().resource("/resource/child", properties);

    baseResource = resource.adaptTo(BaseResource.class);

    assertTrue(CommonValidators.hasChildResource("child", baseResource).isValid());
    assertEquals("Has child resource 'child'.",
        CommonValidators.hasChildResource("child", baseResource).getMessage());
    Assert.assertEquals(ModelValidationMessageType.ERROR,
        CommonValidators.hasChildResource("child", baseResource).getType());

    assertFalse(
        CommonValidators.isChildResourceValidResourceType("child", SampleResourceModel.class,
            baseResource).isValid());
    assertEquals("Has valid child resource 'child'.",
        CommonValidators.isChildResourceValidResourceType("child", SampleResourceModel.class,
            baseResource).getMessage());
    Assert.assertEquals(ModelValidationMessageType.ERROR,
        CommonValidators.isChildResourceValidResourceType("child", SampleResourceModel.class,
            baseResource).getType());

    assertFalse(
        CommonValidators.hasValidChild("child", SampleResourceModel.class, baseResource).isValid());
    assertFalse(
        CommonValidators.hasValidChild("child", SampleResourceModel.class, baseResource).isValid());
    assertEquals("All of the following are true:",
        CommonValidators.hasValidChild("child", SampleResourceModel.class,
            baseResource).getMessage());
    Assert.assertEquals(ModelValidationMessageType.ERROR,
        CommonValidators.hasValidChild("child", SampleResourceModel.class, baseResource).getType());
  }

  @Test
  public void testHasValidChildResourceWhenChildIsMissing() {
    resource = context.create().resource("/resource", properties);

    baseResource = resource.adaptTo(BaseResource.class);
    ModelValidatorBundle bundle = CommonValidators.hasValidChild("child", SampleResourceModel.class,
        baseResource);
    assertFalse(bundle.getValidators().get(0).isValid());
    assertFalse(bundle.getValidators().get(1).isValid());
    assertEquals("All of the following are true:",
        CommonValidators.hasValidChild("child", SampleResourceModel.class,
            baseResource).getMessage());
    Assert.assertEquals(ModelValidationMessageType.ERROR,
        CommonValidators.hasValidChild("child", SampleResourceModel.class, baseResource).getType());
  }

  @Test
  public void testGetFailedErrorValidators() {
    resource = context.create().resource("/resource", properties);

    baseResource = resource.adaptTo(SampleFile.class);

    assertEquals(1, CommonValidators.getFailedErrorValidators(baseResource).size());
    assertFalse(CommonValidators.getFailedErrorValidators(baseResource).get(0).isValid());
    assertEquals(ModelValidationMessageType.ERROR, CommonValidators.getFailedErrorValidators(
        baseResource).get(0).getType());
    assertEquals("Error validator failed for /resource: invalid file type",
        CommonValidators.getFailedErrorValidators(baseResource).get(0).getMessage());
  }

  @Test
  public void testGetFailedErrorValidatorsWhenNoneFail() {
    resource = context.create().resource("/resource.sample", properties);

    baseResource = resource.adaptTo(SampleFile.class);

    assertEquals(0, CommonValidators.getFailedErrorValidators(baseResource).size());
  }

  @Test
  public void testGetFailedWarningValidators() throws InvalidResourceTypeException {
    properties.put("sling:resourceType", "kestros/commons/component");
    resource = context.create().resource("/resource", properties);

    SampleResourceModel sampleResourceModel = SlingModelUtils.adaptTo(resource,
        SampleResourceModel.class);
    sampleResourceModel.validate();
    sampleResourceModel.doDetailedValidation();
    assertEquals(1, CommonValidators.getFailedWarningValidators(sampleResourceModel).size());
    assertFalse(CommonValidators.getFailedWarningValidators(sampleResourceModel).get(0).isValid());
    assertEquals(ModelValidationMessageType.WARNING, CommonValidators.getFailedWarningValidators(
        sampleResourceModel).get(0).getType());
    assertEquals("Warning validator failed for /resource: This is a warning validator.",
        CommonValidators.getFailedWarningValidators(sampleResourceModel).get(0).getMessage());
  }

  @Test
  public void testListContainsNoNullsWhenEmpty() {
    List<Object> list = new ArrayList<>();

    assertTrue(CommonValidators.listContainsNoNulls(list, "message",
        ModelValidationMessageType.ERROR).isValid());
  }

  @Test
  public void testListContainsNoNullsWhenHasNoNulls() {
    List<Object> list = new ArrayList<>();

    list.add("test");
    assertTrue(CommonValidators.listContainsNoNulls(list, "message",
        ModelValidationMessageType.ERROR).isValid());
    assertEquals(ModelValidationMessageType.ERROR,
        CommonValidators.listContainsNoNulls(list, "message",
            ModelValidationMessageType.ERROR).getType());
    assertEquals("message", CommonValidators.listContainsNoNulls(list, "message",
        ModelValidationMessageType.ERROR).getMessage());
  }

  @Test
  public void testListContainsNoNullsWhenHasNull() {
    List<Object> list = new ArrayList<>();

    list.add(null);
    assertFalse(CommonValidators.listContainsNoNulls(list, "message",
        ModelValidationMessageType.ERROR).isValid());
    assertEquals(ModelValidationMessageType.ERROR,
        CommonValidators.listContainsNoNulls(list, "message",
            ModelValidationMessageType.ERROR).getType());
    assertEquals("message", CommonValidators.listContainsNoNulls(list, "message",
        ModelValidationMessageType.ERROR).getMessage());
  }
}