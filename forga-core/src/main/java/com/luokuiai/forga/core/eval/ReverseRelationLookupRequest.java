package com.luokuiai.forga.core.eval;

import com.luokuiai.forga.core.model.ConsistencyToken;
import com.luokuiai.forga.core.model.ObjectRef;
import com.luokuiai.forga.core.model.RelationRef;
import java.util.Objects;
import java.util.Optional;

/**
 * Reverse lookup request for objects containing a relationship subject.
 *
 * @param objectType caller-defined object type to return
 * @param relation relation to resolve in reverse
 * @param subject subject shape to match
 * @param cursor optional resolver cursor
 * @param consistency optional consistency token
 * @param limit maximum returned objects
 */
public record ReverseRelationLookupRequest(
    String objectType,
    RelationRef relation,
    ReverseLookupSubject subject,
    Optional<ListObjectsCursor> cursor,
    Optional<ConsistencyToken> consistency,
    int limit) {

  /**
   * Creates a reverse lookup request without a cursor.
   *
   * @param objectType caller-defined object type to return
   * @param relation relation to resolve in reverse
   * @param subject subject shape to match
   * @param limit maximum returned objects
   */
  public ReverseRelationLookupRequest(
      String objectType, RelationRef relation, ReverseLookupSubject subject, int limit) {
    this(objectType, relation, subject, Optional.empty(), Optional.empty(), limit);
  }

  /**
   * Creates a reverse lookup request.
   *
   * @param objectType caller-defined object type to return
   * @param relation relation to resolve in reverse
   * @param subject subject shape to match
   * @param cursor optional resolver cursor
   * @param consistency optional consistency token
   * @param limit maximum returned objects
   */
  public ReverseRelationLookupRequest {
    objectType = new ObjectRef(objectType, "listing").type();
    relation = Objects.requireNonNull(relation, "relation is required");
    subject = Objects.requireNonNull(subject, "subject is required");
    cursor = cursor == null ? Optional.empty() : cursor;
    consistency = consistency == null ? Optional.empty() : consistency;
    if (limit < 1) {
      throw new IllegalArgumentException("limit must be positive");
    }
  }
}
