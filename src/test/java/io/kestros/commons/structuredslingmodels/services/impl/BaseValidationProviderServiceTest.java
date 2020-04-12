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

package io.kestros.commons.structuredslingmodels.services.impl;

import static org.junit.Assert.assertEquals;

import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.BaseSlingModel;
import io.kestros.commons.structuredslingmodels.validation.DefaultModelValidationService;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class BaseValidationProviderServiceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private BaseValidationProviderService baseValidationProviderService;

  private BaseResource baseResource;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    baseValidationProviderService = new BaseValidationProviderService();
  }

  @Test
  public void testGetModelValidationServiceWhenNull() {
    baseResource = new BaseResource();
    assertEquals(DefaultModelValidationService.class,
        baseValidationProviderService.getModelValidationService(baseResource).getClass());
  }

  @Test
  public void testGetModelValidationServiceWhenNoStructuredModelAnnotation() {
    assertEquals(DefaultModelValidationService.class,
        baseValidationProviderService.getModelValidationService(new BaseSlingModel()).getClass());
  }

  @Test
  public void testGetValidatorsWhenModelValidationServiceIsNull() {
    baseResource = new BaseResource();
    assertEquals(0, baseValidationProviderService.getValidators(baseResource, null).size());
  }

  @Test
  public void testGetBasicValidatorsWhenModelValidationServiceIsNull() {
    baseResource = new BaseResource();
    assertEquals(0, baseValidationProviderService.getBasicValidators(baseResource, null).size());
  }

  @Test
  public void testGetDetailedValidatorsWhenModelValidationServiceIsNull() {
    baseResource = new BaseResource();
    assertEquals(0, baseValidationProviderService.getDetailedValidators(baseResource, null).size());
  }

}