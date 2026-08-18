package com.luokuiai.forga.core.policy;

import com.luokuiai.forga.core.model.PermissionRef;
import java.util.Map;

/**
 * Immutable policy definition keyed by permission.
 *
 * @param permissions permission expressions
 */
public record PolicyDefinition(Map<PermissionRef, PermissionExpression> permissions) {

  /**
   * Creates a policy definition.
   *
   * @param permissions permission expressions
   */
  public PolicyDefinition {
    permissions = Map.copyOf(permissions);
    if (permissions.isEmpty()) {
      throw new IllegalArgumentException("at least one permission is required");
    }
  }
}
