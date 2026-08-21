package com.luokuiai.forga.mybatis;

import com.luokuiai.forga.core.model.SubjectRef;
import java.util.Optional;

/**
 * Supplies the current neutral authorization subject.
 */
@FunctionalInterface
public interface ForgaSubjectProvider {

  /**
   * Returns the current subject.
   *
   * @return current subject when available
   */
  Optional<SubjectRef> currentSubject();
}
