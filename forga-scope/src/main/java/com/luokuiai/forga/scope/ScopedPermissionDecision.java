package com.luokuiai.forga.scope;

import com.luokuiai.forga.core.eval.CheckDecision;

/**
 * Decision for a scope-bound permission check.
 *
 * @param request evaluated scoped permission request
 * @param decision underlying authorization decision
 */
public record ScopedPermissionDecision(
    ScopedPermissionRequest request, CheckDecision decision) {

  /**
   * Creates a scoped permission decision.
   *
   * @param request evaluated scoped permission request
   * @param decision underlying authorization decision
   */
  public ScopedPermissionDecision {
    if (request == null) {
      throw new IllegalArgumentException("request is required");
    }
    if (decision == null) {
      throw new IllegalArgumentException("decision is required");
    }
  }

  /**
   * Returns whether the permission is allowed.
   *
   * @return true when allowed
   */
  public boolean allowed() {
    return decision.allowed();
  }
}
