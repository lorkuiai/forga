package com.luokuiai.forga.spring.web;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

/**
 * Invocation context for a resource-code authorization check.
 *
 * @param resourceCode host-defined resource permission code
 * @param method annotated method, when available
 * @param handler Spring handler object, when available
 * @param request HTTP request, when available
 */
public record ResourceInvocation(
    String resourceCode,
    Optional<Method> method,
    Optional<Object> handler,
    Optional<HttpServletRequest> request) {

  /**
   * Creates a resource invocation without Spring MVC request context.
   *
   * @param resourceCode host-defined resource permission code
   */
  public ResourceInvocation(String resourceCode) {
    this(resourceCode, Optional.empty(), Optional.empty(), Optional.empty());
  }

  /**
   * Creates a resource invocation.
   *
   * @param resourceCode host-defined resource permission code
   * @param method annotated method
   * @param handler Spring handler object
   * @param request HTTP request
   */
  public ResourceInvocation {
    resourceCode = validateResourceCode(resourceCode);
    method = method == null ? Optional.empty() : method;
    handler = handler == null ? Optional.empty() : handler;
    request = request == null ? Optional.empty() : request;
  }

  private static String validateResourceCode(String resourceCode) {
    Objects.requireNonNull(resourceCode, "resource code is required");
    if (resourceCode.isBlank()) {
      throw new IllegalArgumentException("resource code is required");
    }
    return resourceCode;
  }
}
