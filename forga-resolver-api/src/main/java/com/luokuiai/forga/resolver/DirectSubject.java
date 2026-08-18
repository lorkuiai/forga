package com.luokuiai.forga.resolver;

import com.luokuiai.forga.core.model.SubjectRef;
import java.util.Objects;

/**
 * Concrete subject returned by relationship resolution.
 *
 * @param subject concrete subject reference
 */
public record DirectSubject(SubjectRef subject) implements RelationshipSubject {

  /**
   * Creates a direct subject.
   *
   * @param subject concrete subject reference
   */
  public DirectSubject {
    subject = Objects.requireNonNull(subject, "subject is required");
  }
}
