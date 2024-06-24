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

import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Objects;

public class BaseSlingRequestTest {
    @Rule
    public SlingContext context = new SlingContext();

    private BaseRequestContext requestModel;

    @Before
    public void setUp() {
        context.addModelsForPackage("io.kestros");

        requestModel = context.request().adaptTo(BaseRequestContext.class);
    }

    @Test
    public void testGetRequest() {
        assertNotNull(requestModel);
        assertNotNull(requestModel.getRequest());
        assertEquals(context.request(), requestModel.getRequest());
    }

    @Test
    public void testGetResponse() {
        assertNotNull(requestModel);
        assertNotNull(requestModel.getResponse());
        assertEquals(context.response(), requestModel.getResponse());
    }

    @Test
    public void testGetResourceResolver() {
        assertEquals(context.resourceResolver(), requestModel.getResourceResolver());
    }

//    @Test
//    public void testGetBaseResource() {
//        Resource resource = context.create().resource("/resource");
//
//        context.request().setResource(resource);
//
//        requestModel = context.request().adaptTo(BaseRequestContext.class);
//
//        assertEquals("/resource", Objects.requireNonNull(requestModel).getBaseResource().getPath());
//    }
}