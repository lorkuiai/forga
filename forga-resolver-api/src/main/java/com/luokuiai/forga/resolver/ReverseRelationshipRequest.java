package com.luokuiai.forga.resolver;

import com.luokuiai.forga.core.model.RelationRef;
import java.util.Objects;
import java.util.Optional;

/**
 * Request for objects related to a subject.
 *
 * @param objectType caller-defined object type to return
 * @param relation relation to resolve in reverse
 * @param subject subject to match
 * @param cursor optional page cursor
 * @param limit maximum number of returned objects
 * @param context resolver context
 */
public record ReverseRelationshipRequest(
    String objectType,
    RelationRef relation,
    RelationshipSubject subject,
    Optional<PageCursor> cursor,
    int limit,
    ResolverContext context) {

  /**
   * Creates a reverse relationship request with no cursor and an empty resolver context.
   *
   * @param objectType caller-defined object type to return
   * @param relation relation to resolve in reverse
   * @param subject subject to match
   * @param limit maximum number of returned objects
   */
  public ReverseRelationshipRequest(
      String objectType, RelationRef relation, RelationshipSubject subject, int limit) {
    this(objectType, relation, subject, Optional.empty(), limit, ResolverContext.empty());
  }

  /**
   * Creates a reverse relationship request.
   *
   * @param objectType caller-defined object type to return
   * @param relation relation to resolve in reverse
   * @param subject subject to match
   * @param cursor optional page cursor
   * @param limit maximum number of returned objects
   * @param context resolver context
   */
  public ReverseRelationshipRequest {
    objectType = objectType(objectType);
    relation = Objects.requireNonNull(relation, "relation is required");
    subject = Objects.requireNonNull(subject, "subject is required");
    cursor = cursor == null ? Optional.empty() : cursor;
    limit = ResolverBounds.limit(limit);
    context = Objects.requireNonNull(context, "context is required");
  }

  private static String objectType(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("objectType is required");
    }
    return value.trim();
  }
}
