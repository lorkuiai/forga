package com.luokuiai.forga.core.eval;

import com.luokuiai.forga.core.model.SubjectRef;
import java.util.Objects;

/**
 * Direct subject used for reverse relationship lookups.
 *
 * @param subject concrete subject
 */
public record DirectReverseLookupSubject(SubjectRef subject) implements ReverseLookupSubject {

  /**
   * Creates a direct reverse lookup subject.
   *
   * @param subject concrete subject
   */
  public DirectReverseLookupSubject {
    subject = Objects.requireNonNull(subject, "subject is required");
  }
}
