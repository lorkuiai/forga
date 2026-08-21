package com.luokuiai.forga.resolver;

import java.util.List;
import java.util.Objects;

/**
 * Response containing subjects related to an object.
 *
 * @param request request that produced the response
 * @param subjects immutable resolved subjects
 * @param consistency returned consistency context
 */
public record ForwardRelationshipResponse(
    ForwardRelationshipRequest request,
    List<RelationshipSubject> subjects,
    ConsistencyContext consistency) {

  /**
   * Creates a forward relationship response with the request consistency context.
   *
   * @param request request that produced the response
   * @param subjects resolved subjects
   */
  public ForwardRelationshipResponse(
      ForwardRelationshipRequest request, List<RelationshipSubject> subjects) {
    this(request, subjects, request.context().consistency());
  }

  /**
   * Creates a forward relationship response.
   *
   * @param request request that produced the response
   * @param subjects resolved subjects
   * @param consistency returned consistency context
   */
  public ForwardRelationshipResponse {
    request = Objects.requireNonNull(request, "request is required");
    subjects = List.copyOf(subjects);
    if (subjects.size() > request.limit()) {
      throw new IllegalArgumentException("subjects exceed request limit");
    }
    consistency = Objects.requireNonNull(consistency, "consistency is required");
  }
}
