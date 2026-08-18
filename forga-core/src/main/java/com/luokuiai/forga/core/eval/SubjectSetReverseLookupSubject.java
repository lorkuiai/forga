package com.luokuiai.forga.core.eval;

import com.luokuiai.forga.core.model.SubjectSetRef;
import java.util.Objects;

/**
 * Subject-set reference used for reverse relationship lookups.
 *
 * @param subjectSet subject set
 */
public record SubjectSetReverseLookupSubject(SubjectSetRef subjectSet)
    implements ReverseLookupSubject {

  /**
   * Creates a subject-set reverse lookup subject.
   *
   * @param subjectSet subject set
   */
  public SubjectSetReverseLookupSubject {
    subjectSet = Objects.requireNonNull(subjectSet, "subjectSet is required");
  }
}
