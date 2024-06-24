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

import static org.junit.Assert.*;

import org.junit.Test;

public class MatchingResourceTypeNotFoundExceptionTest {

  @Test
  public void testMatchingResourceTypeNotFoundException() {
    MatchingResourceTypeNotFoundException exception = new MatchingResourceTypeNotFoundException("Test Message");
    assertEquals("Unable to retrieve model for Test Message: No matching Sling Model type found.", exception.getMessage());
  }

  @Test
  public void testMatchingResourceTypeNotFoundExceptionWithResourcePath() {
    MatchingResourceTypeNotFoundException exception = new MatchingResourceTypeNotFoundException("/path/to/resource", "Test Message");
    assertEquals("Unable to retrieve model for /path/to/resource: Test Message", exception.getMessage());
  }

  @Test
  public void testMatchingResourceTypeNotFoundExceptionWithResourcePathAndCause() {
    MatchingResourceTypeNotFoundException exception = new MatchingResourceTypeNotFoundException("/path/to/resource", new Exception("Test Cause"));
    assertEquals("Unable to retrieve model for /path/to/resource", exception.getMessage());
  }

  @Test
  public void testMatchingResourceTypeNotFoundExceptionWithResourcePathAndMessage() {
    MatchingResourceTypeNotFoundException exception = new MatchingResourceTypeNotFoundException("/path/to/resource", "Test Message");
    assertEquals("Unable to retrieve model for /path/to/resource: Test Message", exception.getMessage());
  }

  @Test
  public void testMatchingResourceTypeNotFoundExceptionWithResourcePathAndMessageAndCause() {
    MatchingResourceTypeNotFoundException exception = new MatchingResourceTypeNotFoundException("/path/to/resource", "Test Message", new Exception("Test Cause"));
    assertEquals("Unable to retrieve model for /path/to/resource: Test Message", exception.getMessage());
  }



}