package com.luokuiai.forga.core.context;

import com.luokuiai.forga.core.model.SubjectRef;
import java.util.Optional;

/** Supplies the current authenticated authorization subject. */
@FunctionalInterface
public interface AuthenticatedSubjectProvider {

  /**
   * Returns the current authenticated subject.
   *
   * @return current subject when authentication is available
   */
  Optional<SubjectRef> currentSubject();
}
