package com.luokuiai.forga.core.eval;

import com.luokuiai.forga.core.model.ObjectRef;
import com.luokuiai.forga.core.model.RelationRef;
import java.util.Objects;

/**
 * Request to resolve one object relation.
 *
 * @param object object to inspect
 * @param relation relation to resolve
 */
public record RelationLookupRequest(ObjectRef object, RelationRef relation) {

  /**
   * Creates a relation lookup request.
   *
   * @param object object to inspect
   * @param relation relation to resolve
   */
  public RelationLookupRequest {
    object = Objects.requireNonNull(object, "object is required");
    relation = Objects.requireNonNull(relation, "relation is required");
  }
}
