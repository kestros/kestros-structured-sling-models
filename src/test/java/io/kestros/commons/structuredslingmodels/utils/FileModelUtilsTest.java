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


import static io.kestros.commons.structuredslingmodels.utils.FileModelUtils.adaptToFileType;
import static io.kestros.commons.structuredslingmodels.utils.FileModelUtils.getResourceAsFileType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.utilities.SampleFile;
import io.kestros.commons.structuredslingmodels.utilities.SampleFileSecondary;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
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

  private Exception exception;

  @Before
  public void setUp() {
    context.addModelsForPackage("io.kestros.commons");
    properties.put("jcr:primaryType", "nt:file");
  }

  @Test
  public void testAdaptToFileType() throws InvalidResourceTypeException {
    properties.put("jcr:mimeType", "sample/test");
    resource = context.create().resource("/file.sample", properties);
    resource = spy(resource);

    assertNotNull(adaptToFileType(resource.adaptTo(BaseResource.class), SampleFile.class));
    assertEquals(SampleFile.class,
        adaptToFileType(resource.adaptTo(BaseResource.class), SampleFile.class).getClass());
  }

  @Test
  public void testAdaptToFileTypeWhenSecondaryFileType() throws InvalidResourceTypeException {
    properties.put("jcr:mimeType", "sample-secondary/test");
    resource = context.create().resource("/file.sample-secondary", properties);

    assertNotNull(adaptToFileType(resource.adaptTo(BaseResource.class), SampleFileSecondary.class));
  }

  @Test
  public void testAdaptToFileTypeWhenHasFailedValidation() {
    properties.put("jcr:mimeType", "sample/test");
    resource = context.create().resource("/file.sample-secondary", properties);

    try {
      adaptToFileType(resource.adaptTo(BaseResource.class), SampleFile.class);
    } catch (InvalidResourceTypeException e) {
      exception = e;
    }
    assertEquals(
        "Unable to adapt '/file.sample-secondary' to SampleFile: Failed validation checks. "
        + "[invalid file type]",
        exception.getMessage());
  }

  @Test
  public void testAdaptToFileTypeWhenMimeTypeDoesNotMatchExpect() {
    properties.put("jcr:mimeType", "sample-secondary/test");
    resource = context.create().resource("/file.sample", properties);

    try {
      adaptToFileType(resource.adaptTo(BaseResource.class), SampleFile.class);
    } catch (InvalidResourceTypeException e) {
      exception = e;
    }
    assertEquals(
        "Unable to adapt '/file.sample' to SampleFile: File mimeType 'sample-secondary/test' "
        + "did not match any expected types.", exception.getMessage());
  }

  @Test
  public void testAdaptToFileTypeWhenMimeTypeDoesNotMatchExpectAndResourceAdaptionFails() {
    properties.put("jcr:mimeType", "sample-secondary/test");
    resource = context.create().resource("/file.sample", properties);
    resource = spy(resource);
    doReturn(resource.adaptTo(BaseResource.class), resource.adaptTo(SampleFile.class),
        resource.adaptTo(ValueMap.class), null).when(resource).adaptTo(any());

    try {
      adaptToFileType(resource, SampleFile.class);
    } catch (InvalidResourceTypeException e) {
      exception = e;
    }
    assertEquals("InvalidResourceTypeException", exception.getClass().getSimpleName());
    assertEquals("Unable to adapt '/file.sample' to SampleFile: Invalid resource type.",
        exception.getMessage());
  }

  @Test
  public void testAdaptToFileTypeWhenInvalidFileType() {
    resource = context.create().resource("/file.invalid", properties);

    try {
      adaptToFileType(resource.adaptTo(BaseResource.class), SampleFile.class);
    } catch (InvalidResourceTypeException e) {
      exception = e;
    }
    assertEquals(
        "Unable to adapt '/file.invalid' to SampleFile: Failed validation checks. [invalid file "
        + "type]", exception.getMessage());
  }

  @Test
  public void testAdaptToFileTypeWhenPassingResource() throws InvalidResourceTypeException {
    properties.put("jcr:mimeType", "sample/test");
    resource = context.create().resource("/file.sample", properties);

    assertNotNull(adaptToFileType(resource, SampleFile.class));
  }

  @Test
  public void testAdaptToFileTypeWhenPassingResourceAndInvalidFileType() {
    resource = context.create().resource("/file.invalid", properties);

    try {
      adaptToFileType(resource, SampleFile.class);
    } catch (InvalidResourceTypeException e) {
      exception = e;

    }
    assertEquals(
        "Unable to adapt '/file.invalid' to SampleFile: Failed validation checks. [invalid file "
        + "type]", exception.getMessage());
  }

  @Test
  public void testGetResourceAsFileType()
      throws ResourceNotFoundException, InvalidResourceTypeException {
    properties.put("jcr:mimeType", "sample/test");
    context.create().resource("/file.sample", properties);

    assertEquals("file.sample", getResourceAsFileType("/file.sample", context.resourceResolver(),
        SampleFile.class).getName());
  }

  @Test
  public void testGetResourceAsFileTypeWhenSecondaryFileType()
      throws ResourceNotFoundException, InvalidResourceTypeException {
    properties.put("jcr:mimeType", "sample-secondary/test");
    context.create().resource("/file.sample-secondary", properties);

    assertEquals("file.sample-secondary",
        getResourceAsFileType("/file.sample-secondary", context.resourceResolver(),
            SampleFileSecondary.class).getName());
  }

  @Test
  public void testGetChildAsFileType()
      throws ChildResourceNotFoundException, InvalidResourceTypeException {
    properties.put("jcr:mimeType", "sample/test");
    resource = context.create().resource("/resource", properties);
    context.create().resource("/resource/file.sample", properties);

    assertEquals("file.sample",
        FileModelUtils.getChildAsFileType("file.sample", resource, SampleFile.class).getName());
  }

  @Test
  public void testGetChildAsFileTypeWhenPassingBaseResource()
      throws ChildResourceNotFoundException, InvalidResourceTypeException {
    properties.put("jcr:mimeType", "sample/test");
    resource = context.create().resource("/resource", properties);
    context.create().resource("/resource/file.sample", properties);

    assertEquals("file.sample",
        FileModelUtils.getChildAsFileType("file.sample", resource.adaptTo(BaseResource.class),
            SampleFile.class).getName());
  }
}