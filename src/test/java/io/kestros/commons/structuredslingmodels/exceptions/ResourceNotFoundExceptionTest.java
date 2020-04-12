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

package io.kestros.commons.structuredslingmodels.exceptions;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ResourceNotFoundExceptionTest {

  @Test
  public void testGetMessage() {
    ResourceNotFoundException exception = new ResourceNotFoundException("/resource");
    assertEquals("Unable to adapt '/resource': Resource not found.", exception.getMessage());
  }

  @Test
  public void testGetMessageWhenResourceAndMessage() {
    ResourceNotFoundException exception = new ResourceNotFoundException("/resource", "My Message");
    assertEquals("Unable to adapt '/resource': My Message", exception.getMessage());
  }

}