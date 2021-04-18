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

import io.kestros.commons.structuredslingmodels.filetypes.BaseFile;
import io.kestros.commons.structuredslingmodels.filetypes.FileType;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class,
       resourceType = "nt:file")
public class SampleFileSecondary extends BaseFile {

  @PostConstruct
  public void validate() {
    if (!getName().endsWith(".sample-secondary")) {
      //      addErrorMessage("invalid file type");
    }
  }

  @Override
  public FileType getFileType() {
    return new FileType() {
      @Override
      public String getExtension() {
        return "sample-secondary";
      }

      @Override
      public String getOutputContentType() {
        return "sample-secondary/test";
      }

      @Override
      public List<String> getReadableContentTypes() {
        return Collections.singletonList("sample-secondary/test");
      }

      @Override
      public String getName() {
        return "sample-secondary";
      }

      @Override
      public <T extends BaseFile> Class<T> getFileModelClass() {
        return null;
      }
    };
  }
}
