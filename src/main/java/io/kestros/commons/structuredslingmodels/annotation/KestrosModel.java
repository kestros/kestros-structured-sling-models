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

package io.kestros.commons.structuredslingmodels.annotation;

import io.kestros.commons.structuredslingmodels.validation.DefaultModelValidationService;
import io.kestros.commons.structuredslingmodels.validation.ModelValidationService;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for assigning a ModelValidatorService and documentation Resource paths to a Sling
 * Model type.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface KestrosModel {

  /**
   * The ModelValidationService assigned to the current Sling Model type, or the
   * DefaultModelValidationService if none exist.
   *
   * @return The ModelValidationService assigned to the current Sling Model type, or the
   *     DefaultModelValidationService if none exist.
   */
  Class<? extends ModelValidationService> validationService()
      default DefaultModelValidationService.class;

  /**
   * Array of documentation Resource paths.
   *
   * @return Array of documentation Resource paths.
   */
  String[] docPaths() default {};
}
