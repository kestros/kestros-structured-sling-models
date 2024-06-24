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

import io.kestros.commons.structuredslingmodels.BaseResource;
import org.junit.Test;

public class InvalidResourceTypeExceptionTest {

  @Test
  public void testGetMessage() {
    InvalidResourceTypeException exception = new InvalidResourceTypeException("My Message");
    assertEquals("My Message", exception.getMessage());
  }

  @Test
  public void testGetMessageWhenResourceAndMessage() {
    InvalidResourceTypeException exception =
            new InvalidResourceTypeException("/resource", "My Message");
    assertEquals("Unable to adapt '/resource': My Message", exception.getMessage());
  }

  @Test
  public void testGetMessageWhenResourceAndClass() {
    InvalidResourceTypeException exception =
            new InvalidResourceTypeException("/resource", BaseResource.class);
    assertEquals("Unable to adapt '/resource' to BaseResource: Invalid resource type.",
                 exception.getMessage());
  }

  @Test
  public void testGetMessageWhenResourceAndClassAndMessage() {
    InvalidResourceTypeException exception =
            new InvalidResourceTypeException("/resource", BaseResource.class, "My Message");
    assertEquals("Unable to adapt '/resource' to BaseResource: My Message", exception.getMessage());
  }

  @Test
  public void testGetMessageWhenResourceAndClassAndCause() {
    InvalidResourceTypeException exception =
            new InvalidResourceTypeException("/resource", BaseResource.class,
                                             new Exception("My Cause"));
    assertEquals("Unable to adapt '/resource' to BaseResource: Invalid resource type.",
                 exception.getMessage());
  }

  @Test
  public void testGetMessageWhenResourceAndClassAndCauseAndMessage() {
    InvalidResourceTypeException exception =
            new InvalidResourceTypeException("/resource", BaseResource.class,
                                             "My Message", new Exception("My Cause"));
    assertEquals("Unable to adapt '/resource' to BaseResource: My Message", exception.getMessage());
  }

  @Test
  public void testGetMessageWhenResourceAndMessageAndCause() {
    InvalidResourceTypeException exception =
            new InvalidResourceTypeException("/resource", "My Message", new Exception("My Cause"));
    assertEquals("Unable to adapt '/resource': My Message", exception.getMessage());
  }

}