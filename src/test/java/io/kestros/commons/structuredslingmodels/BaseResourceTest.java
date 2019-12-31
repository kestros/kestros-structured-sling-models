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

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

import io.kestros.commons.structuredslingmodels.exceptions.NoParentResourceException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class BaseResourceTest {

  @Rule
  public final SlingContext context = new SlingContext();

  private Resource resource;

  private Resource parentResource;

  private Resource childResource;

  private BaseResource baseResource;

  private Map<String, Object> properties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    parentResource = context.create().resource("/parent");
    childResource = context.create().resource("/parent/child");

    baseResource = parentResource.adaptTo(BaseResource.class);
  }

  @Test
  public void testGetParent() throws NoParentResourceException {
    context.create().resource("/resource");
    resource = context.create().resource("/resource/child");

    baseResource = resource.adaptTo(BaseResource.class);

    assertEquals("/resource", baseResource.getParent().getPath());
  }

  @Test(expected = NoParentResourceException.class)
  public void testGetParentWhenNoParent() throws NoParentResourceException {
    resource = context.create().resource("/resource");

    baseResource = resource.adaptTo(BaseResource.class);

    assertEquals("/", baseResource.getParent().getPath());
    baseResource.getParent().getParent();
  }

  @Test
  public void testGetName() throws Exception {
    assertEquals("parent", baseResource.getName());
  }

  @Test
  public void testGetTitle() {
    properties.put("jcr:title", "Title");
    resource = context.create().resource("/resource-with-title", properties);

    baseResource = resource.adaptTo(BaseResource.class);

    assertEquals("Title", baseResource.getTitle());
  }

  @Test
  public void testGetTitleWhenNotSet() {
    resource = context.create().resource("/resource-without-title");

    baseResource = resource.adaptTo(BaseResource.class);

    assertEquals("resource-without-title", baseResource.getTitle());
  }

  @Test
  public void testGetDescription() {
    properties.put("jcr:description", "Description");
    resource = context.create().resource("/resource-with-description", properties);

    baseResource = resource.adaptTo(BaseResource.class);

    assertEquals("Description", baseResource.getDescription());
  }

  @Test
  public void testGetDescriptionWhenNotSet() {
    resource = context.create().resource("/resource-without-description");

    baseResource = resource.adaptTo(BaseResource.class);

    assertEquals("", baseResource.getDescription());
  }

  @Test
  public void testGetNameWhenNested() throws Exception {
    baseResource = childResource.adaptTo(BaseResource.class);

    assertEquals("child", baseResource.getName());
  }

  @Test
  public void testGetPath() throws Exception {
    assertEquals("/parent", baseResource.getPath());
  }

  @Test
  public void testGetPathWhenNested() throws Exception {
    baseResource = childResource.adaptTo(BaseResource.class);

    assertEquals("/parent/child", baseResource.getPath());
  }

  @Test
  public void testGetPathWithSlashesReplaced() {
    baseResource = childResource.adaptTo(BaseResource.class);

    assertEquals("-parent-child", baseResource.getPathWithReplacedSlashes());
  }

  @Test
  public void testGetProperties() throws Exception {
    Map<String, Object> properties = new HashMap<>();

    childResource = context.create().resource("/parent/child-with-properties", properties);
    baseResource = childResource.adaptTo(BaseResource.class);

    assertNotNull(baseResource.getProperties());
    assertEquals(properties, baseResource.getProperties());
  }

  @Test
  public void testGetProperty() throws Exception {
    Map<String, Object> properties = new HashMap<>();
    properties.put("property", "Value");

    childResource = context.create().resource("/parent/child-with-properties", properties);
    baseResource = childResource.adaptTo(BaseResource.class);

    assertEquals("Value", baseResource.getProperty("property", null));
  }

  @Test
  public void testGetPropertyWhenPropertyIsMissing() throws Exception {
    Map<String, Object> properties = new HashMap<>();

    childResource = context.create().resource("/parent/child-with-properties", properties);
    baseResource = childResource.adaptTo(BaseResource.class);

    assertEquals("Default", baseResource.getProperty("property", "Default"));
  }

  @Test
  public void testGetResourceTypeWhenSlingResourceTypeIsSet() {
    properties.put("sling:resourceType", "resource-type");
    resource = context.create().resource("/resource-with-sling-resource-type", properties);

    baseResource = resource.adaptTo(BaseResource.class);

    assertEquals("resource-type", baseResource.getResourceType());
  }

  @Test
  public void testGetResourceTypeWhenJcrPrimaryTypeIsSet() {
    properties.put("jcr:primaryType", "jcr-primary-type");
    resource = context.create().resource("/resource-with-jcr-primary-type", properties);

    baseResource = resource.adaptTo(BaseResource.class);

    assertEquals("jcr-primary-type", baseResource.getResourceType());
  }

  @Test
  public void testGetJcrPrimaryType() {
    properties.put("jcr:primaryType", "jcr-primary-type");
    resource = context.create().resource("/resource-with-jcr-primary-type", properties);

    baseResource = resource.adaptTo(BaseResource.class);

    assertEquals("jcr-primary-type", baseResource.getJcrPrimaryType());
  }

  @Test
  public void testGetJcrPrimaryTypeWhenNotSet() {
    resource = context.create().resource("/resource-with-jcr-primary-type", properties);

    baseResource = resource.adaptTo(BaseResource.class);

    assertEquals("", baseResource.getJcrPrimaryType());
  }

  @Test
  public void testGetSlingResourceType() {
    properties.put("sling:resourceType", "resource-type");
    resource = context.create().resource("/resource-with-sling-resource-type", properties);

    baseResource = resource.adaptTo(BaseResource.class);

    assertEquals("resource-type", baseResource.getSlingResourceType());
  }

  @Test
  public void testGetSlingResourceTypeWhenEmpty() {
    resource = context.create().resource("/resource-with-sling-resource-type", properties);

    baseResource = resource.adaptTo(BaseResource.class);

    assertEquals("", baseResource.getSlingResourceType());
  }

  @Test
  public void testGetResourceSuperTypeWhenEmpty() {
    assertEquals("", baseResource.getResourceSuperType());
  }

  @Test
  public void testGetResourceSuperTypeWhenSlingResourceTypeIsSet() {
    properties.put("sling:resourceSuperType", "resource-type");
    resource = context.create().resource("/resource-with-sling-resource-super-type", properties);

    baseResource = resource.adaptTo(BaseResource.class);

    assertEquals("resource-type", baseResource.getResourceSuperType());
  }

  @Test
  public void testGetResourceResolver() {
    assertEquals(context.resourceResolver(), baseResource.getResourceResolver());
  }

  @Test
  public void testGetLastModifiedDate() {
    properties.put("jcr:lastModified", new Date(0));
    resource = context.create().resource("/resource", properties);

    baseResource = resource.adaptTo(BaseResource.class);

    assertEquals(new Date(0), baseResource.getLastModifiedDate());
  }


}