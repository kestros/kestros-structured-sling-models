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

import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nonnull;
import org.apache.commons.io.IOUtils;

/**
 * Utility methods working with and retrieving information about Files.
 */
public class FileUtils {

  private FileUtils() {
  }

  /**
   * Size of an input stream, in a readable format.
   *
   * @param inputStream InputStream to retrieve the size of.
   * @return Size of an input stream, in a readable format.
   * @throws IOException InputStream could not be read.
   */
  @Nonnull
  public static String getReadableFileSize(@Nonnull final InputStream inputStream) throws
          IOException {
    final byte[] bytes = IOUtils.toByteArray(inputStream);
    return getReadableFileSize(bytes.length);
  }

  /**
   * Formats a Long to a readable file size format (B,kB, MB,GB,TB ).
   *
   * @param size Number to convert to readable file size format.
   * @return Long to a readable file size format (B,kB, MB,GB,TB ).
   */
  @Nonnull
  private static String getReadableFileSize(@Nonnull final long size) {
    return org.apache.commons.io.FileUtils.byteCountToDisplaySize(size);
  }

}
