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

package io.kestros.commons.structuredslingmodels.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.NoParentResourceException;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.utilities.SampleFile;
import io.kestros.commons.structuredslingmodels.utilities.SampleRequestModel;
import io.kestros.commons.structuredslingmodels.utilities.SampleResourceModel;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.factory.ModelFactory;
import org.apache.sling.models.impl.ModelAdapterFactory;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlingModelUtilsTest {

  @Rule
  public final SlingContext context = new SlingContext();
  private Resource resource;
  private Map<String, Object> properties = new HashMap<>();
  private BaseResource baseResource;

  @Before
  public void initialSetup() {
    context.addModelsForPackage("io.kestros");
  }

  @Before
  public void setUp() throws Exception {
    resource = context.create().resource("/resource");
  }

  @Test
  public void testAdaptTo() throws Exception {
    baseResource = SlingModelUtils.adaptTo(resource, BaseResource.class);

    assertNotNull(resource);
  }

  @Test
  public void testAdaptToWhenUsingSpecificSlingResourceType() throws Exception {
    properties.put("sling:resourceType", "kestros/commons/component");

    resource = context.create().resource("/resource-ui-framework", properties);

    baseResource = SlingModelUtils.adaptTo(resource, SampleResourceModel.class);

    assertNotNull(resource);
    assertNotNull(baseResource);
  }

  @Test(expected = InvalidResourceTypeException.class)
  public void testAdaptToWhenResourceNullsOnAdaption() throws Exception {
    baseResource = SlingModelUtils.adaptTo(resource, SampleRequestModel.class);
  }

  @Test
  public void testAdaptToWhenPassingBaseResource() throws Exception {
    baseResource = SlingModelUtils.adaptTo(resource, BaseResource.class);
    baseResource = SlingModelUtils.adaptTo(baseResource, BaseResource.class);

    assertNotNull(baseResource);
  }

  @Test
  public void testAdaptToWhenModelIsNull() {
    resource = spy(resource);

    try {
      baseResource = SlingModelUtils.adaptTo(resource, SampleRequestModel.class);

    } catch (InvalidResourceTypeException exception) {
    }

    verify(resource, times(6)).getPath();
    assertNull(baseResource);
  }

  @Test
  public void testAdaptToWhenJcrContentIsValid() throws InvalidResourceTypeException {
    properties.put("sling:resourceType", "kestros/commons/component");

    resource = context.create().resource("/resource-ui-framework");
    context.create().resource("/resource-ui-framework/jcr:content", properties);

    resource = spy(resource);

    baseResource = SlingModelUtils.adaptTo(resource, SampleResourceModel.class);

    assertNotNull(resource);
    assertNotNull(baseResource);

    verify(resource, times(3)).getPath();
    assertEquals("/resource-ui-framework", baseResource.getPath());
  }

  @Test(expected = InvalidResourceTypeException.class)
  public void testAdaptToWhenJcrContentIsInvalid() throws InvalidResourceTypeException {
    properties.put("sling:resourceType", "kestros/commons/component");

    resource = context.create().resource("/resource-ui-framework");
    context.create().resource("/resource-ui-framework/jcr:content");

    baseResource = SlingModelUtils.adaptTo(resource, SampleResourceModel.class);
  }

  @Test
  public void testAdaptToWhenJcrContentIsInvalidAndCatchingException() {
    properties.put("sling:resourceType", "kestros/commons/component");

    resource = context.create().resource("/resource-ui-framework");
    context.create().resource("/resource-ui-framework/jcr:content");

    resource = spy(resource);

    try {
      baseResource = SlingModelUtils.adaptTo(resource, SampleResourceModel.class);
    } catch (InvalidResourceTypeException exception) {

    }

    verify(resource, times(3)).getPath();
    assertNull(baseResource);
  }

  @Test
  public void testAdaptToBaseResource() {
    assertEquals("/resource", SlingModelUtils.adaptToBaseResource(resource).getPath());
  }

  @Test
  public void testAdaptToBaseResourceWhenPassingBaseResource() {
    BaseResource baseResource = resource.adaptTo(BaseResource.class);

    assertEquals("/resource", SlingModelUtils.adaptToBaseResource(baseResource).getPath());
  }

  @Test(expected = IllegalStateException.class)
  public void testAdaptToBaseResourceWhenAdaptionReturnsNull() {
    resource = spy(resource);
    doReturn(null).when(resource).adaptTo(BaseResource.class);

    SlingModelUtils.adaptToBaseResource(resource);
  }

  @Test(expected = IllegalStateException.class)
  public void testAdaptToBaseResourceWhenPassingBaseResourceAndAdaptionReturnsNull() {
    resource = spy(resource);
    baseResource = resource.adaptTo(BaseResource.class);
    doReturn(null).when(resource).adaptTo(BaseResource.class);

    SlingModelUtils.adaptToBaseResource(baseResource);
  }

  @Test
  public void testIsValidResourceTypeWhenSlingServletDefault() {
    resource = context.create().resource("/resource-base");

    assertTrue(SlingModelUtils.isValidResourceType(resource, BaseResource.class));
  }

  @Test
  public void testIsValidResourceTypeWhenSlingResourceTypeMatchesModelAnnotation() {
    properties.put("sling:resourceType", "kestros/commons/component");
    resource = context.create().resource("/resource-sample-type", properties);

    assertTrue(SlingModelUtils.isValidResourceType(resource, SampleResourceModel.class));
  }

  @Test
  public void testIsValidResourceTypeWhenSlingResourceTypeDoesNotMatchesModelAnnotation() {
    resource = context.create().resource("/resource-not-sample-type");

    assertFalse(SlingModelUtils.isValidResourceType(resource, SampleResourceModel.class));
  }

  @Test
  public void testIsValidResourceTypeWhenSlingResourceSuperTypeMatches() {
    Map<String, Object> resourceTypeProperties = new HashMap<>();
    resourceTypeProperties.put("sling:resourceSuperType", "kestros/commons/component");

    properties.put("sling:resourceType", "extending-resource-type");

    resource = context.create().resource("/apps/extending-resource-type", resourceTypeProperties);
    resource = context.create().resource(
        "/resource-implementing-extending-resource-type", properties);

    assertTrue(SlingModelUtils.isValidResourceType(resource, SampleResourceModel.class));
  }

  @Test
  public void testIsValidResourceTypeWhenSlingResourceSuperTypeDoesNotMatch() {
    Map<String, Object> resourceTypeProperties = new HashMap<>();
    resourceTypeProperties.put("sling:resourceSuperType", "base-resource-type-non-match");

    properties.put("sling:resourceType", "extending-resource-type");

    resource = context.create().resource("/apps/extending-resource-type", resourceTypeProperties);
    resource = context.create().resource(
        "/resource-implementing-extending-resource-type", properties);

    assertFalse(SlingModelUtils.isValidResourceType(resource, SampleResourceModel.class));
  }

  @Test
  public void testIsValidResourceTypeWhenSlingResourceSuperTypeDoesMatchThroughMultipleLevels() {
    Map<String, Object> secondResourceTypeProperties = new HashMap<>();
    secondResourceTypeProperties.put("sling:resourceSuperType", "kestros/commons/component");
    context.create().resource("/apps/secondary-resource-type", secondResourceTypeProperties);

    Map<String, Object> thirdResourceTypeProperties = new HashMap<>();
    thirdResourceTypeProperties.put("sling:resourceSuperType", "secondary-resource-type");

    context.create().resource("/apps/extending-resource-type", thirdResourceTypeProperties);

    properties.put("sling:resourceType", "extending-resource-type");
    resource = context.create().resource(
        "/resource-implementing-extending-resource-type", properties);

    assertTrue(SlingModelUtils.isValidResourceType(resource, SampleResourceModel.class));
  }


  @Test
  public void testIsValidResourceTypeWhenSlingResourceSuperTypeDoesNotMatchThroughMultipleLevels() {
    Map<String, Object> secondResourceTypeProperties = new HashMap<>();
    secondResourceTypeProperties.put("sling:resourceSuperType", "base-resource-type-non-match");

    Map<String, Object> thirdResourceTypeProperties = new HashMap<>();
    thirdResourceTypeProperties.put("sling:resourceSuperType", "secondary-resource");

    properties.put("sling:resourceType", "extending-resource-type");

    resource = context.create().resource(
        "/apps/secondary-resource-type", secondResourceTypeProperties);
    resource = context.create().resource(
        "/apps/extending-resource-type", thirdResourceTypeProperties);
    resource = context.create().resource(
        "/resource-implementing-extending-resource-type", properties);

    assertFalse(SlingModelUtils.isValidResourceType(resource, SampleResourceModel.class));
  }

  @Test
  public void testGetResourceTypePathWhenApps() {
    resource = context.create().resource("/apps/my-resource");
    assertEquals("my-resource",
                 SlingModelUtils.getResourceTypePath(resource.adaptTo(BaseResource.class)));
  }

  @Test
  public void testGetResourceTypePathWhenLibs() {
    resource = context.create().resource("/libs/my-resource");
    assertEquals("my-resource",
                 SlingModelUtils.getResourceTypePath(resource.adaptTo(BaseResource.class)));
  }


  @Test
  public void testGetChildAsType() throws Exception {
    properties.put("sling:resourceType", "kestros/commons/component");

    resource = context.create().resource("/resource-with-child-framework");
    context.create().resource("/resource-with-child-framework/ui-framework", properties);

    assertNotNull(
        SlingModelUtils.getChildAsType("ui-framework", resource, SampleResourceModel.class));
  }

  @Test
  public void testGetChildAsTypeWhenBaseResourcePassed() throws Exception {
    properties.put("sling:resourceType", "kestros/commons/component");

    resource = context.create().resource("/resource-with-child-framework");
    context.create().resource("/resource-with-child-framework/ui-framework", properties);

    BaseResource baseResource = resource.adaptTo(BaseResource.class);

    assertNotNull(
        SlingModelUtils.getChildAsType("ui-framework", baseResource, SampleResourceModel.class));
  }

  @Test
  public void testGetChildAsTypeWhenHasJcrContent() throws Exception {
    properties.put("sling:resourceType", "kestros/commons/component");

    resource = context.create().resource("/resource-with-child-framework");
    context.create().resource("/resource-with-child-framework/jcr:content");
    context.create().resource(
        "/resource-with-child-framework/jcr:content/ui-framework", properties);

    assertNotNull(
        SlingModelUtils.getChildAsType("ui-framework", resource, SampleResourceModel.class));
  }

  @Test(expected = InvalidResourceTypeException.class)
  public void testGetChildAsTypeWhenHasJcrContentChildIsInvalid() throws Exception {
    properties.put("sling:resourceType", "kestros/commons/component");

    resource = context.create().resource("/resource-with-child-framework");
    context.create().resource("/resource-with-child-framework/jcr:content");
    context.create().resource("/resource-with-child-framework/jcr:content/ui-framework");

    SlingModelUtils.getChildAsType("ui-framework", resource, SampleResourceModel.class);
  }

  @Test(expected = ChildResourceNotFoundException.class)
  public void testGetChildAsTypeWhenHasJcrContentChildIsNotFound() throws Exception {
    properties.put("sling:resourceType", "kestros/commons/component");

    resource = context.create().resource("/resource-with-child-framework");
    context.create().resource("/resource-with-child-framework/jcr:content");

    SlingModelUtils.getChildAsType("missing-resource", resource, SampleResourceModel.class);
  }

  @Test(expected = ChildResourceNotFoundException.class)
  public void testGetChildAsTypeWhenChildNameIsBlank() throws Exception {
    SlingModelUtils.getChildAsType("", resource, SampleResourceModel.class);
  }

  @Test(expected = ChildResourceNotFoundException.class)
  public void testGetChildAsTypeWhenChildResourceDoesNotExist() throws Exception {
    SlingModelUtils.getChildAsType("child-does-not-exist", resource, SampleResourceModel.class);
  }

  @Test
  public void testGetChildAsTypeBaseResource() throws Exception {
    context.create().resource("/resource/child");

    SlingModelUtils.getChildAsBaseResource("child", resource);
  }

  @Test
  public void testGetChildAsTypeBaseResourceWhenPassingBaseResource() throws Exception {
    context.create().resource("/resource/child");

    SlingModelUtils.getChildAsBaseResource("child", resource.adaptTo(BaseResource.class));
  }

  @Test
  public void testGetChildrenAsBaseResource() {
    properties.put("sling:resourceType", "kestros/commons/component");

    resource = context.create().resource("/resource-with-multiple-children");
    context.create().resource("/resource-with-multiple-children/ui-framework-1", properties);
    context.create().resource("/resource-with-multiple-children/ui-framework-2", properties);
    context.create().resource("/resource-with-multiple-children/ui-framework-3", properties);
    context.create().resource("/resource-with-multiple-children/ui-framework-4", properties);
    context.create().resource("/resource-with-multiple-children/unstructured-resource-1");
    context.create().resource("/resource-with-multiple-children/unstructured-resource-2");

    resource = spy(resource);

    List<BaseResource> frameworkList = SlingModelUtils.getChildrenAsBaseResource(resource);

    assertNotNull(frameworkList);
    assertEquals(6, frameworkList.size());
    assertEquals("ui-framework-1", frameworkList.get(0).getName());
    assertEquals("ui-framework-2", frameworkList.get(1).getName());
    assertEquals("ui-framework-3", frameworkList.get(2).getName());
    assertEquals("ui-framework-4", frameworkList.get(3).getName());
    assertEquals("unstructured-resource-1", frameworkList.get(4).getName());
    assertEquals("unstructured-resource-2", frameworkList.get(5).getName());

    verify(resource, times(2)).getPath();
  }

  @Test
  public void testGetChildrenAsBaseResourceWhenPassingBaseResource() {
    properties.put("sling:resourceType", "kestros/commons/component");

    resource = context.create().resource("/resource-with-multiple-children");
    context.create().resource("/resource-with-multiple-children/ui-framework-1", properties);
    context.create().resource("/resource-with-multiple-children/ui-framework-2", properties);
    context.create().resource("/resource-with-multiple-children/ui-framework-3", properties);
    context.create().resource("/resource-with-multiple-children/ui-framework-4", properties);
    context.create().resource("/resource-with-multiple-children/unstructured-resource-1");
    context.create().resource("/resource-with-multiple-children/unstructured-resource-2");

    resource = spy(resource);

    List<BaseResource> frameworkList = SlingModelUtils.getChildrenAsBaseResource(
        resource.adaptTo(BaseResource.class));

    assertNotNull(frameworkList);
    assertEquals(6, frameworkList.size());
    assertEquals("ui-framework-1", frameworkList.get(0).getName());
    assertEquals("ui-framework-2", frameworkList.get(1).getName());
    assertEquals("ui-framework-3", frameworkList.get(2).getName());
    assertEquals("ui-framework-4", frameworkList.get(3).getName());
    assertEquals("unstructured-resource-1", frameworkList.get(4).getName());
    assertEquals("unstructured-resource-2", frameworkList.get(5).getName());

    verify(resource, times(2)).getPath();
  }

  @Test
  public void testGetChildrenOfType() throws Exception {
    properties.put("sling:resourceType", "kestros/commons/component");

    resource = context.create().resource("/resource-with-multiple-children");
    context.create().resource("/resource-with-multiple-children/ui-framework-1", properties);
    context.create().resource("/resource-with-multiple-children/ui-framework-2", properties);
    context.create().resource("/resource-with-multiple-children/ui-framework-3", properties);
    context.create().resource("/resource-with-multiple-children/ui-framework-4", properties);
    context.create().resource("/resource-with-multiple-children/unstructured-resource-1");
    context.create().resource("/resource-with-multiple-children/unstructured-resource-2");

    resource = spy(resource);

    List<SampleResourceModel> frameworkList = SlingModelUtils.getChildrenOfType(
        resource, SampleResourceModel.class);

    assertNotNull(frameworkList);
    assertEquals(4, frameworkList.size());
    assertEquals("ui-framework-1", frameworkList.get(0).getName());
    assertEquals("ui-framework-2", frameworkList.get(1).getName());
    assertEquals("ui-framework-3", frameworkList.get(2).getName());
    assertEquals("ui-framework-4", frameworkList.get(3).getName());

    verify(resource, times(4)).getPath();
  }

  @Test
  public void testGetChildrenOfTypeWhenPassingBaseResource() throws Exception {
    properties.put("sling:resourceType", "kestros/commons/component");

    resource = context.create().resource("/resource-with-multiple-children");
    context.create().resource("/resource-with-multiple-children/ui-framework-1", properties);
    context.create().resource("/resource-with-multiple-children/ui-framework-2", properties);
    context.create().resource("/resource-with-multiple-children/ui-framework-3", properties);
    context.create().resource("/resource-with-multiple-children/ui-framework-4", properties);
    context.create().resource("/resource-with-multiple-children/unstructured-resource-1");
    context.create().resource("/resource-with-multiple-children/unstructured-resource-2");

    BaseResource baseResource = resource.adaptTo(BaseResource.class);

    List<SampleResourceModel> frameworkList = SlingModelUtils.getChildrenOfType(
        baseResource, SampleResourceModel.class);

    assertNotNull(frameworkList);
    assertEquals(4, frameworkList.size());
    assertEquals("ui-framework-1", frameworkList.get(0).getName());
    assertEquals("ui-framework-2", frameworkList.get(1).getName());
    assertEquals("ui-framework-3", frameworkList.get(2).getName());
    assertEquals("ui-framework-4", frameworkList.get(3).getName());
  }

  @Test
  public void testGetChildrenOfTypeWhenInvalidResourceType() throws Exception {
    properties.put("sling:resourceType", "kestros/commons/component");

    resource = spy(Resource.class);
    Resource childResource = spy(
        context.create().resource("/resource-with-multiple-children/unstructured-resource"));

    List<Resource> resources = new ArrayList<>();
    resources.add(childResource);

    when(resource.getChildren()).thenReturn(resources);

    SlingModelUtils.getChildrenOfType(resource, SampleResourceModel.class);

    verify(childResource, times(7)).getPath();
    assertEquals(0, SlingModelUtils.getChildrenOfType(resource, SampleResourceModel.class).size());
  }

  @Test
  public void testGetChildrenOfTypeWhenFilteringAllowedResourceNames() throws Exception {
    properties.put("sling:resourceType", "kestros/commons/component");

    resource = context.create().resource("/resource-with-multiple-children");
    context.create().resource("/resource-with-multiple-children/ui-framework-1", properties);
    context.create().resource("/resource-with-multiple-children/ui-framework-2", properties);
    context.create().resource("/resource-with-multiple-children/ui-framework-3", properties);
    context.create().resource("/resource-with-multiple-children/ui-framework-4", properties);
    context.create().resource("/resource-with-multiple-children/unstructured-resource-1");
    context.create().resource("/resource-with-multiple-children/unstructured-resource-2");

    resource = spy(resource);

    List<SampleResourceModel> frameworkList = SlingModelUtils.getChildrenOfType(
        resource, Arrays.asList(new String[] {"ui-framework-1", "ui-framework-2"}), SampleResourceModel.class);

    assertNotNull(frameworkList);
    assertEquals(2, frameworkList.size());
    assertEquals("ui-framework-1", frameworkList.get(0).getName());
    assertEquals("ui-framework-2", frameworkList.get(1).getName());

    verify(resource, times(4)).getPath();
  }

  @Test
  public void testGetResourceAsType() throws Exception {
    properties.put("sling:resourceType", "kestros/commons/component");

    context.create().resource("/resource-sibling/ui-framework", properties);

    SampleResourceModel framework = SlingModelUtils.getResourceAsType(
        "/resource-sibling/ui-framework", resource.getResourceResolver(),
        SampleResourceModel.class);

    assertNotNull(framework);
    assertEquals("/resource-sibling/ui-framework", framework.getPath());
    assertEquals(SampleResourceModel.class, framework.getClass());
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testGetResourceAsTypeWhenEmptyPath() throws Exception {
    SlingModelUtils.getResourceAsType(
        "", resource.getResourceResolver(), SampleResourceModel.class);
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testGetResourceAsTypeWhenResourceIsNotFound() throws Exception {
    SlingModelUtils.getResourceAsType("/content/nonexistent-resource",
                                      resource.getResourceResolver(), SampleResourceModel.class);
  }

  @Test
  public void testGetResourceAsTypeWhenPassingBaseResource() throws Exception {
    properties.put("sling:resourceType", "kestros/commons/component");

    context.create().resource("/resource-sibling/ui-framework", properties);

    BaseResource baseResource = resource.adaptTo(BaseResource.class);

    SampleResourceModel framework = SlingModelUtils.getResourceAsType(
        "/resource-sibling/ui-framework", baseResource.getResourceResolver(),
        SampleResourceModel.class);

    assertNotNull(framework);
    assertEquals("/resource-sibling/ui-framework", framework.getPath());
    assertEquals(SampleResourceModel.class, framework.getClass());
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testGetResourceAsTypeWhenPathIsEmpty() throws Exception {
    SlingModelUtils.getResourceAsType(
        "", resource.getResourceResolver(), SampleResourceModel.class);
  }

  @Test(expected = InvalidResourceTypeException.class)
  public void testGetResourceAsTypeWhenResourceCannotBeAdapted() throws Exception {
    SlingModelUtils.getResourceAsType(
        "/resource", resource.getResourceResolver(), SampleRequestModel.class);
  }

  @Test
  public void testGetResourceAsTypeRelativeResourcePathWhenLibs() throws Exception {
    context.create().resource("/libs/resource");

    Resource syntheticResource = mock(Resource.class);
    when(syntheticResource.getResourceType()).thenReturn("sling:syntheticResourceProviderResource");

    ResourceResolver resolver = context.resourceResolver();
    resolver = spy(resolver);

    when(resolver.getResource("resource")).thenReturn(syntheticResource);

    assertNotNull(SlingModelUtils.getResourceAsType("resource", resource.getResourceResolver(),
                                                    BaseResource.class));

    assertEquals("/libs/resource",
                 SlingModelUtils.getResourceAsType("resource", resolver, BaseResource.class)
                                .getPath());
  }

  @Test
  public void testGetResourceAsTypeFromRelativeResourcePathWhenApps() throws Exception {
    context.create().resource("/apps/resource");

    Resource syntheticResource = mock(Resource.class);
    when(syntheticResource.getResourceType()).thenReturn("sling:syntheticResourceProviderResource");

    ResourceResolver resolver = context.resourceResolver();
    resolver = spy(resolver);

    when(resolver.getResource("resource")).thenReturn(syntheticResource);

    assertNotNull(SlingModelUtils.getResourceAsType("resource", resource.getResourceResolver(),
                                                    BaseResource.class));

    assertEquals("/apps/resource",
                 SlingModelUtils.getResourceAsType("resource", resolver, BaseResource.class)
                                .getPath());
  }

  @Test
  public void testGetResourceAsTypeFromRelativeResourcePathWhenLibsAndAppsExist() throws Exception {
    context.create().resource("/libs/resource");
    context.create().resource("/apps/resource");

    Resource syntheticResource = mock(Resource.class);
    when(syntheticResource.getResourceType()).thenReturn("sling:syntheticResourceProviderResource");

    ResourceResolver resolver = context.resourceResolver();
    resolver = spy(resolver);

    when(resolver.getResource("resource")).thenReturn(syntheticResource);

    assertNotNull(SlingModelUtils.getResourceAsType("resource", resource.getResourceResolver(),
                                                    BaseResource.class));

    assertEquals("/apps/resource",
                 SlingModelUtils.getResourceAsType("resource", resolver, BaseResource.class)
                                .getPath());
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testGetResourceAsTypeFromRelativeResourcePathThatDoesNotExist() throws Exception {
    Resource syntheticResource = mock(Resource.class);
    when(syntheticResource.getResourceType()).thenReturn("sling:syntheticResourceProviderResource");

    ResourceResolver resolver = context.resourceResolver();
    resolver = spy(resolver);

    when(resolver.getResource("nonexistent-resource")).thenReturn(syntheticResource);

    SlingModelUtils.getResourceAsType("nonexistent-resource", resolver, BaseResource.class);
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testGetResourceAsTypeFromRelativeResourcePathThatDoesNotExistSyntheticsFound()
      throws Exception {
    Resource syntheticResource = mock(Resource.class);
    when(syntheticResource.getResourceType()).thenReturn("sling:syntheticResourceProviderResource");

    ResourceResolver resolver = context.resourceResolver();
    resolver = spy(resolver);

    when(resolver.getResource("nonexistent-resource")).thenReturn(syntheticResource);
    when(resolver.getResource("/apps/nonexistent-resource")).thenReturn(syntheticResource);
    when(resolver.getResource("/libs/nonexistent-resource")).thenReturn(syntheticResource);

    SlingModelUtils.getResourceAsType("nonexistent-resource", resolver, BaseResource.class);
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testGetResourceAsTypeFromRelativeResourcePathThatDoesNotExistLibSyntheticFound()
      throws Exception {
    Resource syntheticResource = mock(Resource.class);
    when(syntheticResource.getResourceType()).thenReturn("sling:syntheticResourceProviderResource");

    ResourceResolver resolver = context.resourceResolver();
    resolver = spy(resolver);

    when(resolver.getResource("nonexistent-resource")).thenReturn(syntheticResource);
    when(resolver.getResource("/libs/nonexistent-resource")).thenReturn(syntheticResource);

    SlingModelUtils.getResourceAsType("nonexistent-resource", resolver, BaseResource.class);
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testGetResourceAsTypeFromRelativeResourcePathThatDoesNotExistAppsSyntheticFound()
      throws Exception {
    Resource syntheticResource = mock(Resource.class);
    when(syntheticResource.getResourceType()).thenReturn("sling:syntheticResourceProviderResource");

    ResourceResolver resolver = context.resourceResolver();
    resolver = spy(resolver);

    when(resolver.getResource("nonexistent-resource")).thenReturn(syntheticResource);
    when(resolver.getResource("/apps/nonexistent-resource")).thenReturn(syntheticResource);

    SlingModelUtils.getResourceAsType("nonexistent-resource", resolver, BaseResource.class);
  }

  @Test
  public void testGetResourcesAsType() {
    context.create().resource("/resource-1", properties);
    context.create().resource("/resource-2", properties);
    context.create().resource("/resource-3", properties);

    List<String> paths = Arrays.asList(new String[]{"/resource-1", "/resource-2", "/resource-3"});

    assertEquals(3, SlingModelUtils
                        .getResourcesAsType(paths, context.resourceResolver(), BaseResource.class)
                        .size());
  }

  @Test
  public void testGetResourcesAsTypeWhenInvalid() {
    context.create().resource("/resource-1", properties);
    context.create().resource("/resource-2", properties);
    context.create().resource("/resource-3", properties);

    List<String> paths = Arrays.asList(new String[]{"/resource-1", "/resource-2", "/resource-3"});

    assertEquals(0, SlingModelUtils
                        .getResourcesAsType(paths, context.resourceResolver(), SampleFile.class)
                        .size());
  }

  @Test
  public void testGetResourcesAsTypeWhenNotFound() {

    List<String> paths = Arrays.asList(new String[]{"/resource-1", "/resource-2", "/resource-3"});

    assertEquals(0, SlingModelUtils
                        .getResourcesAsType(paths, context.resourceResolver(), SampleFile.class)
                        .size());
  }

  @Test
  public void testGetParentResourceAsType() throws Exception {
    properties.put("sling:resourceType", "kestros/commons/component");

    context.create().resource("/parent-framework", properties);
    resource = context.create().resource("/parent-framework/child");

    SampleResourceModel framework = SlingModelUtils.getParentResourceAsType(
        resource, SampleResourceModel.class);

    assertNotNull(framework);
    assertEquals("/parent-framework", framework.getPath());
    assertEquals(SampleResourceModel.class, framework.getClass());
  }

  @Test
  public void testGetParentResourceAsTypeWhenPassingBaseResource() throws Exception {
    properties.put("sling:resourceType", "kestros/commons/component");

    context.create().resource("/parent-framework", properties);
    resource = context.create().resource("/parent-framework/child");

    BaseResource baseResource = resource.adaptTo(BaseResource.class);

    SampleResourceModel framework = SlingModelUtils.getParentResourceAsType(
        baseResource, SampleResourceModel.class);

    assertNotNull(framework);
    assertEquals("/parent-framework", framework.getPath());
    assertEquals(SampleResourceModel.class, framework.getClass());
  }

  @Test(expected = NoParentResourceException.class)
  public void testGetParentResourceAsTypeWhenNoParent() throws Exception {
    resource = context.create().resource("/orphan-resource");

    resource = resource.getParent();

    SlingModelUtils.getParentResourceAsType(resource, BaseResource.class);
  }

  @Test
  public void testGetParentResourceAsBaseResource() throws Exception {
    properties.put("sling:resourceType", "kestros/commons/component");

    context.create().resource("/parent-framework", properties);
    resource = context.create().resource("/parent-framework/child");

    BaseResource baseResource = SlingModelUtils.getParentResourceAsBaseResource(resource);

    assertNotNull(baseResource);
    assertEquals("/parent-framework", baseResource.getPath());
    assertEquals(BaseResource.class, baseResource.getClass());
  }


  @Test
  public void testGetFirstAncestorOfType() throws NoValidAncestorException {
    Map<String, Object> properties = new HashMap<>();
    properties.put("sling:resourceType", "kestros/commons/component");

    context.create().resource("/parent");
    context.create().resource("/parent/child", properties);
    context.create().resource("/parent/child/grand-child");
    resource = context.create().resource("/parent/child/grand-child/great-grand-child");

    assertEquals("/parent/child",
                 SlingModelUtils.getFirstAncestorOfType(resource, SampleResourceModel.class)
                                .getPath());
    assertEquals("/parent/child", SlingModelUtils
                                      .getFirstAncestorOfType(resource.adaptTo(BaseResource.class),
                                                              SampleResourceModel.class).getPath());
  }

  @Test
  public void testGetFirstAncestorOfTypeWhenPassedResourceIsValidType()
      throws NoValidAncestorException {
    Map<String, Object> properties = new HashMap<>();
    properties.put("sling:resourceType", "kestros/commons/component");

    context.create().resource("/parent");
    context.create().resource("/parent/child", properties);
    context.create().resource("/parent/child/grand-child");
    resource = context.create().resource("/parent/child/grand-child/great-grand-child", properties);

    assertEquals("/parent/child",
                 SlingModelUtils.getFirstAncestorOfType(resource, SampleResourceModel.class)
                                .getPath());
    assertEquals("/parent/child", SlingModelUtils
                                      .getFirstAncestorOfType(resource.adaptTo(BaseResource.class),
                                                              SampleResourceModel.class).getPath());
  }

  @Test
  public void testGetFirstAncestorOfTypeWhenImmediateParentIsValidType()
      throws NoValidAncestorException {
    Map<String, Object> properties = new HashMap<>();
    properties.put("sling:resourceType", "kestros/commons/component");

    context.create().resource("/parent");
    context.create().resource("/parent/child", properties);
    context.create().resource("/parent/child/grand-child", properties);
    resource = context.create().resource("/parent/child/grand-child/great-grand-child", properties);

    assertEquals("/parent/child/grand-child",
                 SlingModelUtils.getFirstAncestorOfType(resource, SampleResourceModel.class)
                                .getPath());
    assertEquals(
        "/parent/child/grand-child", SlingModelUtils.getFirstAncestorOfType(
            resource.adaptTo(BaseResource.class), SampleResourceModel.class).getPath());
  }

  @Test(expected = NoValidAncestorException.class)
  public void testGetFirstAncestorOfTypeWhenNoValidResourcesAreFound()
      throws NoValidAncestorException {

    context.create().resource("/parent");
    context.create().resource("/parent/child");
    context.create().resource("/parent/child/grand-child");
    resource = context.create().resource("/parent/child/grand-child/great-grand-child");

    SlingModelUtils.getFirstAncestorOfType(resource, SampleResourceModel.class);
    SlingModelUtils.getFirstAncestorOfType(
        resource.adaptTo(BaseResource.class), SampleResourceModel.class);
  }

  @Test
  public void testGetAllDescendantsOfType() throws Exception {
    properties.put("sling:resourceType", "kestros/commons/component");

    resource = context.create().resource("/grand-parent");

    context.create().resource("/grand-parent/parent-framework-1", properties);
    context.create().resource("/grand-parent/parent-framework-1/child-framework", properties);
    context.create().resource("/grand-parent/parent-framework-1/child-unstructured");
    context.create().resource("/grand-parent/parent-framework-2", properties);
    context.create().resource("/grand-parent/parent-framework-2/child-framework", properties);
    context.create().resource("/grand-parent/parent-framework-2/child-unstructured");
    context.create().resource("/grand-parent/parent-unstructured-1");
    context.create().resource("/grand-parent/parent-unstructured-1/child-framework", properties);
    context.create().resource("/grand-parent/parent-unstructured-1/child-unstructured");
    context.create().resource("/grand-parent/parent-unstructured-2");
    context.create().resource("/grand-parent/parent-unstructured-2/child-framework", properties);
    context.create().resource("/grand-parent/parent-unstructured-2/child-unstructured");

    List<SampleResourceModel> SampleResourceModelList = SlingModelUtils.getAllDescendantsOfType(
        resource, SampleResourceModel.class);

    assertNotNull(SampleResourceModelList);
    assertEquals(6, SampleResourceModelList.size());
    assertEquals("/grand-parent/parent-framework-1", SampleResourceModelList.get(0).getPath());
    assertEquals("/grand-parent/parent-framework-2", SampleResourceModelList.get(1).getPath());
    assertEquals("/grand-parent/parent-framework-1/child-framework",
                 SampleResourceModelList.get(2).getPath());
    assertEquals("/grand-parent/parent-framework-2/child-framework",
                 SampleResourceModelList.get(3).getPath());
    assertEquals("/grand-parent/parent-unstructured-1/child-framework",
                 SampleResourceModelList.get(4).getPath());
    assertEquals("/grand-parent/parent-unstructured-2/child-framework",
                 SampleResourceModelList.get(5).getPath());

  }

  @Test
  public void testGetAllDescendantsOfTypeWhenPassingBaseResource() throws Exception {
    properties.put("sling:resourceType", "kestros/commons/component");

    resource = context.create().resource("/grand-parent");

    context.create().resource("/grand-parent/parent-framework-1", properties);
    context.create().resource("/grand-parent/parent-framework-1/child-framework", properties);
    context.create().resource("/grand-parent/parent-framework-1/child-unstructured");
    context.create().resource("/grand-parent/parent-framework-2", properties);
    context.create().resource("/grand-parent/parent-framework-2/child-framework", properties);
    context.create().resource("/grand-parent/parent-framework-2/child-unstructured");
    context.create().resource("/grand-parent/parent-unstructured-1");
    context.create().resource("/grand-parent/parent-unstructured-1/child-framework", properties);
    context.create().resource("/grand-parent/parent-unstructured-1/child-unstructured");
    context.create().resource("/grand-parent/parent-unstructured-2");
    context.create().resource("/grand-parent/parent-unstructured-2/child-framework", properties);
    context.create().resource("/grand-parent/parent-unstructured-2/child-unstructured");

    BaseResource baseResource = resource.adaptTo(BaseResource.class);

    List<SampleResourceModel> SampleResourceModelList = SlingModelUtils.getAllDescendantsOfType(
        baseResource, SampleResourceModel.class);

    assertNotNull(SampleResourceModelList);
    assertEquals(6, SampleResourceModelList.size());
    assertEquals("/grand-parent/parent-framework-1", SampleResourceModelList.get(0).getPath());
    assertEquals("/grand-parent/parent-framework-2", SampleResourceModelList.get(1).getPath());
    assertEquals("/grand-parent/parent-framework-1/child-framework",
                 SampleResourceModelList.get(2).getPath());
    assertEquals("/grand-parent/parent-framework-2/child-framework",
                 SampleResourceModelList.get(3).getPath());
    assertEquals("/grand-parent/parent-unstructured-1/child-framework",
                 SampleResourceModelList.get(4).getPath());
    assertEquals("/grand-parent/parent-unstructured-2/child-framework",
                 SampleResourceModelList.get(5).getPath());

  }


  @Test
  public void testGetResourceAsClosestType() throws InvalidResourceTypeException {

    properties.put("sling:resourceType", "kestros/commons/component");
    resource = context.create().resource("/sample-resource", properties);

    ModelAdapterFactory modelAdapterFactory = mock(ModelAdapterFactory.class);

    when(modelAdapterFactory.getModelFromResource(resource)).thenReturn(
        resource.adaptTo(SampleResourceModel.class));

    assertNotNull(SlingModelUtils.getResourceAsClosestType(resource, modelAdapterFactory));

    assertEquals("/sample-resource",
                 SlingModelUtils.getResourceAsClosestType(resource, modelAdapterFactory).getPath());
    assertEquals("SampleResourceModel", SlingModelUtils.getResourceAsClosestType(
        resource, modelAdapterFactory).getClass().getSimpleName());
  }

  @Test(expected = InvalidResourceTypeException.class)
  public void testGetResourceAsClosestTypeWhenNotExtendingBaseResource()
      throws InvalidResourceTypeException {

    properties.put("sling:resourceType", "kestros/commons/component");
    resource = context.create().resource("/sample-resource", properties);

    ModelAdapterFactory modelAdapterFactory = mock(ModelAdapterFactory.class);

    when(modelAdapterFactory.getModelFromResource(resource)).thenReturn(
        resource.adaptTo(Resource.class));

    assertNull(SlingModelUtils.getResourceAsClosestType(resource, modelAdapterFactory));
  }

  @Test
  public void testGetChildrenAsClosestTypes() {

    Map<String, Object> fileProperties = new HashMap<>();
    context.create().resource("/resource/child-1", fileProperties);
    context.create().resource("/resource/child-2", fileProperties);
    context.create().resource("/resource/child-3", fileProperties);

    ModelFactory factory = mock(ModelFactory.class);

    when(factory.getModelFromResource(any(Resource.class))).thenReturn(
        resource.adaptTo(BaseResource.class));

    assertEquals(3, SlingModelUtils.getChildrenAsClosestTypes(resource, factory).size());
  }

  @Test
  public void testGetChildrenAsClosestTypesWhenNonBaseResourceReturned() {

    Map<String, Object> fileProperties = new HashMap<>();
    context.create().resource("/resource/child-1", fileProperties);
    context.create().resource("/resource/child-2", fileProperties);
    context.create().resource("/resource/child-3", fileProperties);

    ModelFactory factory = mock(ModelFactory.class);

    when(factory.getModelFromResource(any(Resource.class))).thenReturn(resource);

    assertEquals(0, SlingModelUtils.getChildrenAsClosestTypes(resource, factory).size());
  }

  @Test
  public void testGetChildrenAsClosestTypesWhenNullModelFactory() {

    Map<String, Object> fileProperties = new HashMap<>();
    context.create().resource("/resource/child-1", fileProperties);
    context.create().resource("/resource/child-2", fileProperties);
    context.create().resource("/resource/child-3", fileProperties);

    assertEquals(0, SlingModelUtils.getChildrenAsClosestTypes(resource, null).size());
  }

}