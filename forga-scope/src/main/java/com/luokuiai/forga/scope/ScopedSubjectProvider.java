package com.luokuiai.forga.scope;

import java.util.Optional;

/**
 * Provides the scoped subject for the current integration request.
 */
@FunctionalInterface
public interface ScopedSubjectProvider {

  /**
   * Returns the current scoped subject, when one is available.
   *
   * @return scoped subject
   */
  Optional<ScopedSubject> scopedSubject();
}
