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

import java.util.List;

/**
 * Interface used for providing File type detailed to {@link BaseFile} Models.
 */
public interface FileType {

  /**
   * File type's extension. Should not include '.' prefix.
   *
   * @return File type's extension.
   */
  String getExtension();

  /**
   * Content type of this file's output.
   *
   * @return Content type of this file's output.
   */
  String getOutputContentType();

  /**
   * Content types (read from jcr:mimeType) that can be interpreted by this FileType and its
   * associated Model.
   *
   * @return Content types (read from jcr:mimeType) that can be interpreted by this FileType and its
   *     associated Model.
   */
  List<String> getReadableContentTypes();

  /**
   * Name of the file type.
   *
   * @return Name of the file type.
   */
  String getName();

  /**
   * Sling Model class associated to this FileType.
   *
   * @param <T> extends {@link BaseFile}
   * @return Sling Model class associated to this FileType.
   */
  <T extends BaseFile> Class<T> getFileModelClass();
}
