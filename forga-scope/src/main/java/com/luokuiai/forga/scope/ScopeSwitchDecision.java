package com.luokuiai.forga.scope;

import com.luokuiai.forga.core.eval.CheckDecision;
import java.util.Optional;

/**
 * Decision for an attempted active-scope switch.
 *
 * @param request evaluated switch request
 * @param decision underlying authorization decision
 * @param activeScope active scope when the switch is allowed
 */
public record ScopeSwitchDecision(
    ScopeSwitchRequest request, CheckDecision decision, Optional<ActiveScope> activeScope) {

  /**
   * Creates a switch decision.
   *
   * @param request evaluated switch request
   * @param decision underlying authorization decision
   * @param activeScope active scope when allowed
   */
  public ScopeSwitchDecision {
    if (request == null) {
      throw new IllegalArgumentException("request is required");
    }
    if (decision == null) {
      throw new IllegalArgumentException("decision is required");
    }
    activeScope = activeScope == null ? Optional.empty() : activeScope;
  }

  /**
   * Returns whether the switch is allowed.
   *
   * @return true when allowed
   */
  public boolean allowed() {
    return decision.allowed();
  }
}
