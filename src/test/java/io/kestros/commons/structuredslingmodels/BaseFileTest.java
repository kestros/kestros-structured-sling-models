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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import io.kestros.commons.structuredslingmodels.exceptions.JcrFileReadException;
import io.kestros.commons.structuredslingmodels.utils.SampleFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class BaseFileTest {

  @Rule
  public final SlingContext context = new SlingContext();


  private SampleFile baseFile;

  private Resource resource;

  private Map<String, Object> properties = new HashMap<>();
  private Map<String, Object> jcrContentProperties = new HashMap<>();

  @Before
  public void setUp() {
    context.addModelsForPackage("io.kestros");
  }


  @Test
  public void testGetJcrDataInputStream() throws JcrFileReadException {
    properties.put("jcr:primaryType", "nt:file");
    InputStream inputStream = new ByteArrayInputStream("file-contents".getBytes());

    jcrContentProperties.put("jcr:data", inputStream);
    resource = context.create().resource("/file", properties);

    context.create().resource("/file/jcr:content", jcrContentProperties);
    baseFile = resource.adaptTo(SampleFile.class);

    assertNotNull(Objects.requireNonNull(baseFile).getJcrDataInputStream());
  }

  @Test
  public void  testGetJcrDataInputStreamWhenDataPropertyIsMissing() {
    properties.put("jcr:primaryType", "nt:file");
    resource = context.create().resource("/file", properties);

    context.create().resource("/file/jcr:content", jcrContentProperties);
    baseFile = resource.adaptTo(SampleFile.class);

    Exception exception = null;
    try {
      baseFile.getJcrDataInputStream();
    } catch (JcrFileReadException e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals("Unable to read '/file': No jcr:data property found on /file", exception.getMessage());
  }


  @Test
  public void  testGetJcrDataInputStreamWhenDataPropertyValueIsNull() {
    properties.put("jcr:primaryType", "nt:file");
    properties.put("jcr:data", null);
    resource = context.create().resource("/file", properties);

    context.create().resource("/file/jcr:content", jcrContentProperties);
    baseFile = resource.adaptTo(SampleFile.class);

    Exception exception = null;
    try {
      baseFile.getJcrDataInputStream();
    } catch (JcrFileReadException e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals("Unable to read '/file': No jcr:data property found on /file", exception.getMessage());
  }
  @Test
  public void testGetBufferedReader() throws JcrFileReadException {
    properties.put("jcr:primaryType", "nt:file");
    InputStream inputStream = new ByteArrayInputStream("file-contents".getBytes());

    jcrContentProperties.put("jcr:data", inputStream);
    resource = context.create().resource("/file", properties);

    context.create().resource("/file/jcr:content", jcrContentProperties);
    baseFile = resource.adaptTo(SampleFile.class);

    assertNotNull(Objects.requireNonNull(baseFile).getBufferedReader());
  }

  @Test
  public void testGetOutput() throws Exception {
    properties.put("jcr:primaryType", "nt:file");
    InputStream inputStream = new ByteArrayInputStream("file-contents".getBytes());

    jcrContentProperties.put("jcr:data", inputStream);
    resource = context.create().resource("/file.txt", properties);

    context.create().resource("/file.txt/jcr:content", jcrContentProperties);

    baseFile = resource.adaptTo(SampleFile.class);

    assertEquals("file-contents", Objects.requireNonNull(baseFile).getFileContent());
  }

  @Test
  public void testGetOutputWhenMultipleLines() throws Exception {
    properties.put("jcr:primaryType", "nt:file");
    InputStream inputStream = new ByteArrayInputStream(
        "file-contents\nmore-contents\nthird-line".getBytes());

    jcrContentProperties.put("jcr:data", inputStream);
    resource = context.create().resource("/file.txt", properties);

    context.create().resource("/file.txt/jcr:content", jcrContentProperties);

    baseFile = resource.adaptTo(SampleFile.class);

    assertEquals("file-contents\nmore-contents\nthird-line", Objects.requireNonNull(baseFile).getFileContent());
  }

  @Test
  public void testGetFileSize() throws JcrFileReadException, IOException {
    properties.put("jcr:primaryType", "nt:file");
    InputStream inputStream = new ByteArrayInputStream(
        "file-contents\nmore-contents\nthird-line".getBytes());

    jcrContentProperties.put("jcr:data", inputStream);
    resource = context.create().resource("/file.txt", properties);

    context.create().resource("/file.txt/jcr:content", jcrContentProperties);

    baseFile = resource.adaptTo(SampleFile.class);

    assertEquals("38 bytes", Objects.requireNonNull(baseFile).getFileSize());
  }

  @Test
  public void testGetFileSizeWhenIoException() throws JcrFileReadException, IOException {
    properties.put("jcr:primaryType", "nt:file");
    InputStream inputStream = mock(ByteArrayInputStream.class);

    when(inputStream.read(any())).thenThrow(new IOException("Test Exception"));
    resource = context.create().resource("/file.txt", properties);

    baseFile = resource.adaptTo(SampleFile.class);
    baseFile = spy(Objects.requireNonNull(baseFile));

    Exception exception = null;
    doReturn(inputStream).when(baseFile).getJcrDataInputStream();
    try {
       baseFile.getFileSize();
    } catch (IOException e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals("Test Exception", exception.getMessage());
  }

  @Test
  public void testGetExtension() {
    properties.put("jcr:primaryType", "nt:file");
    InputStream inputStream = new ByteArrayInputStream(
        "file-contents\nmore-contents\nthird-line".getBytes());

    jcrContentProperties.put("jcr:data", inputStream);
    resource = context.create().resource("/file.txt", properties);

    context.create().resource("/file.txt/jcr:content", jcrContentProperties);

    baseFile = resource.adaptTo(SampleFile.class);

    assertEquals("txt", Objects.requireNonNull(baseFile).getExtension());
  }
}