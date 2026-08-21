package com.luokuiai.forga.scope;

import java.util.Optional;

/**
 * Provides the active scope for the current integration request.
 */
@FunctionalInterface
public interface ActiveScopeProvider {

  /**
   * Returns the current active scope, when one has been selected.
   *
   * @return active scope
   */
  Optional<ActiveScope> activeScope();
}
