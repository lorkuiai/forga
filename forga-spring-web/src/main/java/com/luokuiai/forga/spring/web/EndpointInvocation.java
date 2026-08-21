package com.luokuiai.forga.spring.web;

import com.luokuiai.forga.core.model.PermissionRef;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Spring Web invocation context supplied to host endpoint authorization.
 *
 * @param permission required permission
 * @param method handler method
 * @param handler Spring handler object
 * @param request current HTTP request
 */
public record EndpointInvocation(
    PermissionRef permission, Method method, Object handler, HttpServletRequest request) {

  /**
   * Creates an endpoint invocation.
   *
   * @param permission required permission
   * @param method handler method
   * @param handler Spring handler object
   * @param request current HTTP request
   */
  public EndpointInvocation {
    permission = Objects.requireNonNull(permission, "permission is required");
    method = Objects.requireNonNull(method, "method is required");
    handler = Objects.requireNonNull(handler, "handler is required");
    request = Objects.requireNonNull(request, "request is required");
  }
}
