package com.luokuiai.forga.spring.web;

/** Host-owned adapter that maps endpoint permissions to Forga authorization decisions. */
@FunctionalInterface
public interface EndpointPermissionAuthorizer {

  /**
   * Authorizes one endpoint invocation.
   *
   * @param invocation endpoint invocation
   * @return authorization decision
   */
  EndpointAuthorizationDecision authorize(EndpointInvocation invocation);
}
