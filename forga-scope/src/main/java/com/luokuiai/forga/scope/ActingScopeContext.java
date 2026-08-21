package com.luokuiai.forga.scope;

import com.luokuiai.forga.core.model.SubjectRef;
import java.util.Optional;

/**
 * Current acting context for scope-bound authorization.
 *
 * @param originalSubject subject that started the request
 * @param actingSubject subject used for authorization decisions
 * @param activeScope active scope selected for the acting subject
 */
public record ActingScopeContext(
    SubjectRef originalSubject, SubjectRef actingSubject, ActiveScope activeScope) {

  /**
   * Creates an acting scope context.
   *
   * @param originalSubject subject that started the request
   * @param actingSubject subject used for authorization decisions
   * @param activeScope active scope selected for the acting subject
   */
  public ActingScopeContext {
    if (originalSubject == null) {
      throw new IllegalArgumentException("original subject is required");
    }
    if (actingSubject == null) {
      throw new IllegalArgumentException("acting subject is required");
    }
    if (activeScope == null) {
      throw new IllegalArgumentException("active scope is required");
    }
  }

  /**
   * Returns whether authorization is evaluated as a different subject.
   *
   * @return true when original and acting subjects differ
   */
  public boolean actingAs() {
    return !originalSubject.equals(actingSubject);
  }

  /**
   * Converts this context to a scoped subject for authorization checks.
   *
   * @return acting scoped subject
   */
  public ScopedSubject scopedSubject() {
    return new ScopedSubject(actingSubject, Optional.of(activeScope));
  }
}
