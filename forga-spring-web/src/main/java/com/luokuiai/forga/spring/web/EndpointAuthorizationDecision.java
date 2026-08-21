package com.luokuiai.forga.spring.web;

import com.luokuiai.forga.core.model.PermissionRef;
import java.util.Objects;

/**
 * Host endpoint authorization result.
 *
 * @param permission checked permission
 * @param allowed whether access is allowed
 * @param reason stable host or Forga decision reason
 */
public record EndpointAuthorizationDecision(
    PermissionRef permission, boolean allowed, String reason) {

  /**
   * Creates a validated decision.
   *
   * @param permission checked permission
   * @param allowed whether access is allowed
   * @param reason stable host or Forga decision reason
   */
  public EndpointAuthorizationDecision {
    permission = Objects.requireNonNull(permission, "permission is required");
    if (reason == null || reason.isBlank()) {
      throw new IllegalArgumentException("reason is required");
    }
    reason = reason.trim();
  }

  /**
   * Creates an allowed decision.
   *
   * @param permission checked permission
   * @return allowed decision
   */
  public static EndpointAuthorizationDecision allowed(PermissionRef permission) {
    return new EndpointAuthorizationDecision(permission, true, "ALLOWED");
  }

  /**
   * Creates a denied decision.
   *
   * @param permission checked permission
   * @param reason denial reason
   * @return denied decision
   */
  public static EndpointAuthorizationDecision denied(PermissionRef permission, String reason) {
    return new EndpointAuthorizationDecision(permission, false, reason);
  }
}
