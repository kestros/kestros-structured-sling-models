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
import static org.junit.Assert.assertNotNull;

import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.utilities.SampleFile;
import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class FileModelUtilsTest {

  @Rule
  public SlingContext context = new SlingContext();

  private SampleFile sampleFile;

  private Resource resource;

  private Map<String, Object> properties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros.commons");
    properties.put("jcr:primaryType", "nt:file");
  }

  @Test
  public void testAdaptToFileType() throws Exception {
    resource = context.create().resource("/file.sample", properties);

    assertNotNull(
        FileModelUtils.adaptToFileType(resource.adaptTo(BaseResource.class), SampleFile.class));
  }

  @Test(expected = InvalidResourceTypeException.class)
  public void testAdaptToFileTypeWhenInvalidFileType() throws Exception {
    resource = context.create().resource("/file.invalid", properties);

    FileModelUtils.adaptToFileType(resource.adaptTo(BaseResource.class), SampleFile.class);
  }


  @Test
  public void testAdaptToFileTypeWhenPassingResource() throws Exception {
    resource = context.create().resource("/file.sample", properties);

    assertNotNull(FileModelUtils.adaptToFileType(resource, SampleFile.class));
  }

  @Test(expected = InvalidResourceTypeException.class)
  public void testAdaptToFileTypeWhenPassingResourceAndInvalidFileType() throws Exception {
    resource = context.create().resource("/file.invalid", properties);

    FileModelUtils.adaptToFileType(resource, SampleFile.class);
  }

  @Test
  public void testGetResourceAsFileType()
      throws InvalidResourceTypeException, ResourceNotFoundException {
    context.create().resource("/file.sample", properties);

    assertEquals("file.sample", FileModelUtils
        .getResourceAsFileType("/file.sample", context.resourceResolver(), SampleFile.class)
        .getName());
  }

  @Test
  public void testGetChildAsFileType()
      throws ChildResourceNotFoundException, InvalidResourceTypeException {
    resource = context.create().resource("/resource", properties);
    context.create().resource("/resource/file.sample", properties);

    assertEquals("file.sample", FileModelUtils.getChildAsFileType("file.sample", resource,
        SampleFile.class).getName());
  }

  @Test
  public void testGetChildAsFileTypeWhenPassingBaseResource()
      throws ChildResourceNotFoundException, InvalidResourceTypeException {
    resource = context.create().resource("/resource", properties);
    context.create().resource("/resource/file.sample", properties);

    assertEquals("file.sample",
        FileModelUtils.getChildAsFileType("file.sample", resource.adaptTo(BaseResource.class),
            SampleFile.class).getName());
  }

}