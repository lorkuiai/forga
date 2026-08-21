package com.luokuiai.forga.resolver;

import com.luokuiai.forga.core.model.ObjectRef;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Response containing objects related to a subject.
 *
 * @param request request that produced the response
 * @param objects immutable resolved objects
 * @param nextCursor optional continuation cursor
 * @param consistency returned consistency context
 */
public record ReverseRelationshipResponse(
    ReverseRelationshipRequest request,
    List<ObjectRef> objects,
    Optional<PageCursor> nextCursor,
    ConsistencyContext consistency) {

  /**
   * Creates a reverse relationship response with no continuation cursor.
   *
   * @param request request that produced the response
   * @param objects resolved objects
   */
  public ReverseRelationshipResponse(ReverseRelationshipRequest request, List<ObjectRef> objects) {
    this(request, objects, Optional.empty(), request.context().consistency());
  }

  /**
   * Creates a reverse relationship response.
   *
   * @param request request that produced the response
   * @param objects resolved objects
   * @param nextCursor optional continuation cursor
   * @param consistency returned consistency context
   */
  public ReverseRelationshipResponse {
    request = Objects.requireNonNull(request, "request is required");
    objects = List.copyOf(objects);
    if (objects.size() > request.limit()) {
      throw new IllegalArgumentException("objects exceed request limit");
    }
    nextCursor = nextCursor == null ? Optional.empty() : nextCursor;
    consistency = Objects.requireNonNull(consistency, "consistency is required");
  }
}
