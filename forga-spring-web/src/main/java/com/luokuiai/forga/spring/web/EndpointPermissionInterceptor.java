package com.luokuiai.forga.spring.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/** Resolves and enforces permissions for Spring MVC handler methods. */
public final class EndpointPermissionInterceptor implements HandlerInterceptor {

  private final EndpointPermissionResolver resolver;

  private final EndpointPermissionAuthorizer authorizer;

  /**
   * Creates an endpoint permission interceptor.
   *
   * @param resolver endpoint permission resolver
   * @param authorizer endpoint permission authorizer
   */
  public EndpointPermissionInterceptor(
      EndpointPermissionResolver resolver, EndpointPermissionAuthorizer authorizer) {
    this.resolver = Objects.requireNonNull(resolver, "resolver is required");
    this.authorizer = Objects.requireNonNull(authorizer, "authorizer is required");
  }

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    if (!(handler instanceof HandlerMethod handlerMethod)) {
      return true;
    }
    EndpointPermissionRequirement requirement =
        resolver
            .resolve(handlerMethod, request)
            .orElseThrow(EndpointAuthorizationException::unresolved);
    if (requirement.isPermitAll()) {
      return true;
    }
    EndpointInvocation invocation =
        new EndpointInvocation(
            requirement.permission().orElseThrow(),
            handlerMethod.getMethod(),
            handlerMethod.getBean(),
            request);
    EndpointAuthorizationDecision decision = authorizer.authorize(invocation);
    if (decision == null) {
      decision =
          EndpointAuthorizationDecision.denied(
              invocation.permission(), "AUTHORIZER_RETURNED_NULL");
    }
    if (!decision.allowed()) {
      throw EndpointAuthorizationException.denied(decision);
    }
    return true;
  }
}
