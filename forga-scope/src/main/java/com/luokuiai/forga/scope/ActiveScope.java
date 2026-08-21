package com.luokuiai.forga.scope;

import java.util.Map;

/**
 * Scope selected for the current request.
 *
 * @param scope selected scope
 * @param attributes immutable integration attributes for the active scope
 */
public record ActiveScope(ScopeRef scope, Map<String, String> attributes) {

  /**
   * Creates an active scope without attributes.
   *
   * @param scope selected scope
   */
  public ActiveScope(ScopeRef scope) {
    this(scope, Map.of());
  }

  /**
   * Creates an active scope.
   *
   * @param scope selected scope
   * @param attributes integration attributes
   */
  public ActiveScope {
    if (scope == null) {
      throw new IllegalArgumentException("scope is required");
    }
    attributes = Map.copyOf(attributes);
  }
}
