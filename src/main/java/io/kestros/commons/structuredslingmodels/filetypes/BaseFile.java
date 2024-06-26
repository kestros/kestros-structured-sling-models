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

package io.kestros.commons.structuredslingmodels.filetypes;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.jackrabbit.JcrConstants.JCR_DATA;
import static org.apache.jackrabbit.JcrConstants.JCR_MIMETYPE;
import static org.apache.jackrabbit.JcrConstants.NT_FILE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kestros.commons.structuredslingmodels.BasePage;
import io.kestros.commons.structuredslingmodels.exceptions.JcrFileReadException;
import io.kestros.commons.structuredslingmodels.utils.FileUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Baseline class for adapting a nt:file Resources and reading their contents.
 */
@Model(adaptables = Resource.class,
        resourceType = NT_FILE)
public abstract class BaseFile extends BasePage {

  private static final Logger LOG = LoggerFactory.getLogger(BaseFile.class);

  /**
   * {@link FileType} associated to the BaseFile implementation model. Used for checking resource
   * validity when adapting Resources to this File model.
   *
   * @return {@link FileType} associated to the BaseFile implementation model.
   */
  @Nonnull
  public abstract FileType getFileType();

  /**
   * Extension of the current file resource.
   *
   * @return Extension of the current file resource.
   */
  @Nonnull
  public String getExtension() {
    String filename = getName();
    if (filename.contains(".")) {
      return filename.substring(filename.lastIndexOf('.') + 1);
    }
    return StringUtils.EMPTY;
  }

  /**
   * jcr:mimeType property value.
   *
   * @return jcr:mimeType property value.
   */
  @Nonnull
  public String getMimeType() {
    return getProperties().get(JCR_MIMETYPE, StringUtils.EMPTY);
  }

  /**
   * Content of the current file as an InputStream. Pulls from jcr:data property of the *
   * jcr:content Resource.
   *
   * @return Content of the current file as an InputStream. Pulls from jcr:data property of the
   *         jcr:content Resource.
   * @throws JcrFileReadException Thrown when there is an error reading the File.
   */
  @Nonnull
  @JsonIgnore
  public InputStream getJcrDataInputStream() throws JcrFileReadException {
    if (!getProperties().containsKey(JCR_DATA)) {
      throw new JcrFileReadException(getPath(), "No jcr:data property found on " + getPath());
    }
    InputStream inputStream = getProperties().get(JCR_DATA, InputStream.class);
    if (inputStream == null) {
      throw new JcrFileReadException(getPath(), "Unable to read jcr:data property on " + getPath());
    }
    return inputStream;
  }

  /**
   * Content of the current File, as a Buffered Reader.
   *
   * @return Content of the current File, as a Buffered Reader.
   * @throws JcrFileReadException Thrown when there is an error reading the File.
   */
  @Nonnull
  @JsonIgnore
  public BufferedReader getBufferedReader() throws JcrFileReadException {
    return new BufferedReader(new InputStreamReader(getJcrDataInputStream(), UTF_8));
  }

  /**
   * Content of the current File, as a String.
   *
   * @return Content of the current File, as a String.
   * @throws IOException Thrown when there is an error reading contents of the File.
   * @throws JcrFileReadException Thrown when there is an error reading the File.
   */
  @Nonnull
  @JsonIgnore
  public String getFileContent() throws IOException, JcrFileReadException {
    final StringBuilder builder = new StringBuilder();

    final BufferedReader bufferedReader = getBufferedReader();

    String line;
    int lineNumber = 0;
    while ((line = bufferedReader.readLine()) != null) {
      if (lineNumber > 0) {
        builder.append('\n');
      }
      builder.append(line);

      lineNumber++;
    }

    return builder.toString();
  }

  /**
   * Size of the current File.
   *
   * @return Size of the current File.
   * @throws JcrFileReadException Thrown when there is an error reading the File.
   * @throws IOException Thrown when there is an error reading contents of the File.
   */
  @Nonnull
  public String getFileSize() throws JcrFileReadException, IOException {
    return FileUtils.getReadableFileSize(getJcrDataInputStream());
  }
}
