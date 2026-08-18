package com.luokuiai.forga.core.eval;

import com.luokuiai.forga.core.model.ObjectRef;
import com.luokuiai.forga.core.model.RelationRef;
import com.luokuiai.forga.core.model.SubjectRef;
import java.util.Objects;

/**
 * One relation proof step used in an allowed decision.
 *
 * @param object object inspected by the proof
 * @param relation relation that matched
 * @param subject subject matched by the relation
 */
public record ProofStep(ObjectRef object, RelationRef relation, SubjectRef subject) {

  /**
   * Creates a proof step.
   *
   * @param object object inspected by the proof
   * @param relation relation that matched
   * @param subject subject matched by the relation
   */
  public ProofStep {
    object = Objects.requireNonNull(object, "object is required");
    relation = Objects.requireNonNull(relation, "relation is required");
    subject = Objects.requireNonNull(subject, "subject is required");
  }
}
