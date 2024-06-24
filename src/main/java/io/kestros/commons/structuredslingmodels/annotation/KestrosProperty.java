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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

/**
 * Designated the annotated method as a property. Can be used for documentation purposes.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface KestrosProperty {

  /**
   * Description of the annotated method.
   *
   * @return Description of the annotated method.
   */
  @Nullable
  String description();

  /**
   * JCR Property used to configure the property.
   *
   * @return JCR Property used to configure the property.
   */
  @Nonnull
  String jcrPropertyName() default StringUtils.EMPTY;

  /**
   * Property's default value.
   *
   * @return Property's default value.
   */
  @Nullable
  String defaultValue() default StringUtils.EMPTY;

  /**
   * Whether to mark the property as configurable, for documentation purposes.
   *
   * @return Whether to mark the property as configurable, for documentation purposes.
   */
  boolean configurable() default false;

  /**
   * Sample value of the JCR Property, to be used for documentation purposes.
   *
   * @return Sample value of the JCR Property, to be used for documentation purposes.
   */
  @Nullable
  String sampleValue() default StringUtils.EMPTY;
}