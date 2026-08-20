package com.luokuiai.forga.spring.web;

import com.luokuiai.forga.core.eval.CheckDecision;
import java.util.Objects;

/**
 * Resource-code authorization decision returned by host integrations.
 *
 * @param resourceCode checked resource permission code
 * @param allowed whether access is allowed
 * @param reason stable host or Forga denial reason
 */
public record ResourceAuthorizationDecision(String resourceCode, boolean allowed, String reason) {

  /**
   * Creates an allowed resource decision.
   *
   * @param resourceCode checked resource permission code
   * @return allowed decision
   */
  public static ResourceAuthorizationDecision allowed(String resourceCode) {
    return new ResourceAuthorizationDecision(resourceCode, true, "ALLOWED");
  }

  /**
   * Creates a denied resource decision.
   *
   * @param resourceCode checked resource permission code
   * @param reason denial reason
   * @return denied decision
   */
  public static ResourceAuthorizationDecision denied(String resourceCode, String reason) {
    return new ResourceAuthorizationDecision(resourceCode, false, reason);
  }

  /**
   * Creates a resource decision from a core check decision.
   *
   * @param resourceCode checked resource permission code
   * @param decision core check decision
   * @return resource decision
   */
  public static ResourceAuthorizationDecision from(String resourceCode, CheckDecision decision) {
    Objects.requireNonNull(decision, "decision is required");
    return new ResourceAuthorizationDecision(
        resourceCode, decision.allowed(), decision.reason().name());
  }

  /**
   * Creates a resource authorization decision.
   *
   * @param resourceCode checked resource permission code
   * @param allowed whether access is allowed
   * @param reason stable host or Forga denial reason
   */
  public ResourceAuthorizationDecision {
    resourceCode = validateResourceCode(resourceCode);
    if (reason == null || reason.isBlank()) {
      reason = allowed ? "ALLOWED" : "DENIED";
    }
  }

  private static String validateResourceCode(String resourceCode) {
    Objects.requireNonNull(resourceCode, "resource code is required");
    if (resourceCode.isBlank()) {
      throw new IllegalArgumentException("resource code is required");
    }
    return resourceCode;
  }
}
