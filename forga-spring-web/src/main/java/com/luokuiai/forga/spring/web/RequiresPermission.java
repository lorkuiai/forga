package com.luokuiai.forga.spring.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Declares a host-defined permission required by a Spring Web entry point. */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {

  /**
   * Returns the host-defined permission code.
   *
   * @return permission code
   */
  String value();
}
