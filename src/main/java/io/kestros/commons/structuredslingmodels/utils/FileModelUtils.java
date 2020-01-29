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

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.adaptTo;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.adaptToBaseResource;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsBaseResource;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsBaseResource;

import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.filetypes.BaseFile;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

public class FileModelUtils {

  private FileModelUtils() {
  }

  /**
   * Adapts a BaseResource to a specified Model type that extends BaseFile.  Adapted object must
   * pass validation ( no errors ).
   *
   * @param fileResource Resource to adapt.
   * @param type Model type to adapt the resource to. Must extend BaseFile.
   * @param <T> Model type to adapt the resource to. Must extend BaseFile.
   * @return The Resource adapted to the specified FileType.
   * @throws InvalidResourceTypeException Thrown when the Resource cannot be adapted to the
   *     passed type, or has an error after adaption.
   */
  public static <T extends BaseFile> T adaptToFileType(BaseResource fileResource, Class<T> type)
      throws InvalidResourceTypeException {
    T file = adaptTo(fileResource, type);
    if (!file.getErrorMessages().isEmpty()) {
      throw new InvalidResourceTypeException(fileResource.getPath(), type,
          "Failed validation checks. " + file.getErrorMessages());
    } else if (!file.getFileType().getReadableContentTypes().contains(file.getMimeType())) {
      file = fileResource.getResource().adaptTo(type);
      if (file != null) {
        throw new InvalidResourceTypeException(fileResource.getPath(), type,
            "File mimeType '" + file.getMimeType() + "' did not match any expected types.");
      } else {
        throw new InvalidResourceTypeException(fileResource.getPath(), type);
      }
    }
    return file;
  }

  /**
   * Adapts a Resource to a specified Model type that extends BaseFile.  Adapted object must pass
   * validation ( no errors ).
   *
   * @param fileResource Resource to adapt.
   * @param type Model type to adapt the resource to. Must extend BaseFile.
   * @param <T> Model type to adapt the resource to. Must extend BaseFile.
   * @return The Resource adapted to the specified FileType.
   * @throws InvalidResourceTypeException Thrown when the Resource cannot be adapted to the
   *     passed type, or has an error after adaption.
   */
  public static <T extends BaseFile> T adaptToFileType(Resource fileResource, Class<T> type)
      throws InvalidResourceTypeException {
    return adaptToFileType(adaptToBaseResource(fileResource), type);
  }

  public static <T extends BaseFile> T getResourceAsFileType(String path, ResourceResolver resolver,
      Class<T> type) throws ResourceNotFoundException, InvalidResourceTypeException {
    return adaptToFileType(getResourceAsBaseResource(path, resolver), type);
  }

  public static <T extends BaseFile> T getChildAsFileType(String name, Resource resource,
      Class<T> type) throws ChildResourceNotFoundException, InvalidResourceTypeException {
    return adaptToFileType(getChildAsBaseResource(name, resource), type);
  }

  public static <T extends BaseFile> T getChildAsFileType(String name, BaseResource baseResource,
      Class<T> type) throws ChildResourceNotFoundException, InvalidResourceTypeException {
    return getChildAsFileType(name, baseResource.getResource(), type);
  }


}
