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

package io.kestros.commons.structuredslingmodels.utilities;

import io.kestros.commons.structuredslingmodels.filetypes.BaseFile;
import io.kestros.commons.structuredslingmodels.filetypes.FileType;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class,
       resourceType = "nt:file")
public class SampleFile extends BaseFile {

  @PostConstruct
  public void validate() {
    if (!getName().endsWith(".sample")) {
      addErrorMessage("invalid file type");
    }
  }

  @Override
  public FileType getFileType() {
    return new FileType() {
      @Override
      public String getExtension() {
        return "sample";
      }

      @Override
      public String getOutputContentType() {
        return "sample/test";
      }

      @Override
      public List<String> getReadableContentTypes() {
        return Arrays.asList("sample/test");
      }

      @Override
      public String getName() {
        return "sample";
      }

      @Override
      public <T extends BaseFile> Class<T> getFileModelClass() {
        return null;
      }
    };
  }
}
