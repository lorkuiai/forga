package com.luokuiai.forga.spring.web;

/**
 * Host-owned adapter that maps resource codes to Forga authorization checks.
 */
@FunctionalInterface
public interface ResourceCheckAdapter {

  /**
   * Checks the resource required by an invocation.
   *
   * @param invocation resource invocation context
   * @return resource authorization decision
   */
  ResourceAuthorizationDecision check(ResourceInvocation invocation);
}
