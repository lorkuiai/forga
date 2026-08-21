package com.luokuiai.forga.scope;

import com.luokuiai.forga.core.model.SubjectRef;
import java.util.Optional;

/**
 * Subject plus the active scope selected for request authorization.
 *
 * @param subject subject to authorize
 * @param activeScope active scope, if one has been selected
 */
public record ScopedSubject(SubjectRef subject, Optional<ActiveScope> activeScope) {

  /**
   * Creates a scoped subject.
   *
   * @param subject subject to authorize
   * @param activeScope active scope, if one has been selected
   */
  public ScopedSubject {
    if (subject == null) {
      throw new IllegalArgumentException("subject is required");
    }
    activeScope = activeScope == null ? Optional.empty() : activeScope;
  }

  /**
   * Creates a scoped subject with an active scope.
   *
   * @param subject subject to authorize
   * @param activeScope active scope
   * @return scoped subject
   */
  public static ScopedSubject of(SubjectRef subject, ActiveScope activeScope) {
    return new ScopedSubject(subject, Optional.of(activeScope));
  }

  /**
   * Creates a scoped subject without an active scope.
   *
   * @param subject subject to authorize
   * @return scoped subject
   */
  public static ScopedSubject withoutScope(SubjectRef subject) {
    return new ScopedSubject(subject, Optional.empty());
  }
}
