package com.luokuiai.forga.resolver;

import java.util.List;

/**
 * Bounded batch of reverse relationship requests.
 *
 * @param requests immutable requests
 */
public record ReverseRelationshipBatchRequest(List<ReverseRelationshipRequest> requests) {

  /**
   * Creates a reverse relationship batch request.
   *
   * @param requests reverse requests
   */
  public ReverseRelationshipBatchRequest {
    requests = List.copyOf(requests);
    ResolverBounds.batchSize(requests.size());
  }
}
