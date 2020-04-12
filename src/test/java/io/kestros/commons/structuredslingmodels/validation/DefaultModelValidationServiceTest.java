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

import io.kestros.commons.structuredslingmodels.BaseResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class DefaultModelValidationServiceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private DefaultModelValidationService service;
  private Resource resource;

  @Before
  public void setUp() throws Exception {
    service = new DefaultModelValidationService();
    context.addModelsForPackage("io.kestros");
  }

  @Test
  public void testGetModel() throws Exception {
    resource = context.create().resource("/resource");

    BaseResource baseResource = resource.adaptTo(BaseResource.class);
    service.setModel(baseResource);

    Assert.assertEquals(BaseResource.class, service.getModel().getClass());
  }

  @Test(expected = IllegalStateException.class)
  public void testGetModelWhenGenericModelIsNotInstanceOfBaseResource() throws Exception {
    resource = context.create().resource("/resource");

    service.setModel(null);

    Assert.assertEquals(BaseResource.class, service.getModel().getClass());
  }

  @Test
  public void testRegisterBasicValidators() throws Exception {
    service.registerBasicValidators();
  }

  @Test
  public void testRegisterDetailedValidators() throws Exception {
    service.registerDetailedValidators();
  }
}