package com.luokuiai.forga.core.model;

/**
 * Opaque reference to an actor or member candidate.
 *
 * @param type caller-defined subject type
 * @param id caller-defined stable subject id
 */
public record SubjectRef(String type, String id) {

  /**
   * Creates a subject reference.
   *
   * @param type caller-defined subject type
   * @param id caller-defined stable subject id
   */
  public SubjectRef {
    type = ReferenceValidator.kind("subject type", type);
    id = ReferenceValidator.value("subject id", id);
  }
}
