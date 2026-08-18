package com.luokuiai.forga.core.model;

import java.util.Objects;

/**
 * Reference to every subject related to an object through a relation.
 *
 * @param object referenced object
 * @param relation referenced relation
 */
public record SubjectSetRef(ObjectRef object, RelationRef relation) {

  /**
   * Creates a subject-set reference.
   *
   * @param object referenced object
   * @param relation referenced relation
   */
  public SubjectSetRef {
    object = Objects.requireNonNull(object, "object is required");
    relation = Objects.requireNonNull(relation, "relation is required");
  }
}
