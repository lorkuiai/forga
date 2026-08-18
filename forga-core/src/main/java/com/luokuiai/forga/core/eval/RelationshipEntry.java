package com.luokuiai.forga.core.eval;

import com.luokuiai.forga.core.model.SubjectRef;
import com.luokuiai.forga.core.model.SubjectSetRef;
import java.util.Objects;
import java.util.Optional;

/**
 * Relationship member returned by a lookup.
 */
public final class RelationshipEntry {

  private final SubjectRef subject;

  private final SubjectSetRef subjectSet;

  private RelationshipEntry(SubjectRef subject, SubjectSetRef subjectSet) {
    this.subject = subject;
    this.subjectSet = subjectSet;
  }

  /**
   * Creates a direct subject entry.
   *
   * @param subject direct subject
   * @return relationship entry
   */
  public static RelationshipEntry subject(SubjectRef subject) {
    return new RelationshipEntry(Objects.requireNonNull(subject, "subject is required"), null);
  }

  /**
   * Creates a subject-set entry.
   *
   * @param subjectSet subject set
   * @return relationship entry
   */
  public static RelationshipEntry subjectSet(SubjectSetRef subjectSet) {
    return new RelationshipEntry(
        null, Objects.requireNonNull(subjectSet, "subjectSet is required"));
  }

  /**
   * Returns direct subject if present.
   *
   * @return direct subject
   */
  public Optional<SubjectRef> subject() {
    return Optional.ofNullable(subject);
  }

  /**
   * Returns subject set if present.
   *
   * @return subject set
   */
  public Optional<SubjectSetRef> subjectSet() {
    return Optional.ofNullable(subjectSet);
  }
}
