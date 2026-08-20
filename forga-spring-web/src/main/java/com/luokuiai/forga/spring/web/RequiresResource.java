package com.luokuiai.forga.spring.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a host-defined resource permission required by a Spring Web entry point.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresResource {

  /**
   * Marker for methods that explicitly require no resource permission.
   */
  String NONE = "NONE";

  /**
   * Host-defined resource permission code, for example {@code rsc:meeting:view}.
   *
   * @return resource permission code
   */
  String value() default NONE;
}
