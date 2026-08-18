package com.luokuiai.forga.core.eval;

import com.luokuiai.forga.core.model.ObjectRef;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Result of an authorized object listing request.
 *
 * @param request listing request
 * @param objects authorized objects
 * @param nextCursor optional continuation cursor
 * @param successful true when listing completed without fail-closed rejection
 * @param reason stable result reason
 */
public record ListObjectsResponse(
    ListObjectsRequest request,
    List<ObjectRef> objects,
    Optional<ListObjectsCursor> nextCursor,
    boolean successful,
    DecisionReason reason) {

  /**
   * Creates a listing response.
   *
   * @param request listing request
   * @param objects authorized objects
   * @param nextCursor optional continuation cursor
   * @param successful true when listing completed without fail-closed rejection
   * @param reason stable result reason
   */
  public ListObjectsResponse {
    request = Objects.requireNonNull(request, "request is required");
    objects = List.copyOf(objects);
    nextCursor = nextCursor == null ? Optional.empty() : nextCursor;
    reason = Objects.requireNonNull(reason, "reason is required");
  }

  static ListObjectsResponse success(
      ListObjectsRequest request, List<ObjectRef> objects, Optional<ListObjectsCursor> cursor) {
    return new ListObjectsResponse(request, objects, cursor, true, DecisionReason.ALLOWED);
  }

  static ListObjectsResponse failure(ListObjectsRequest request, DecisionReason reason) {
    return new ListObjectsResponse(request, List.of(), Optional.empty(), false, reason);
  }
}
