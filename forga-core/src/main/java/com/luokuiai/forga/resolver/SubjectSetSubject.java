package com.luokuiai.forga.resolver;

import com.luokuiai.forga.core.model.SubjectSetRef;
import java.util.Objects;

/**
 * Subject-set reference returned by relationship resolution.
 *
 * @param subjectSet subject-set reference
 */
public record SubjectSetSubject(SubjectSetRef subjectSet) implements RelationshipSubject {

  /**
   * Creates a subject-set subject.
   *
   * @param subjectSet subject-set reference
   */
  public SubjectSetSubject {
    subjectSet = Objects.requireNonNull(subjectSet, "subjectSet is required");
  }
}
