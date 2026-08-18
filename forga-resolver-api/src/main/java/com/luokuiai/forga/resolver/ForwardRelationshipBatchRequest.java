package com.luokuiai.forga.resolver;

import java.util.List;

/**
 * Bounded batch of forward relationship requests.
 *
 * @param requests immutable requests
 */
public record ForwardRelationshipBatchRequest(List<ForwardRelationshipRequest> requests) {

  /**
   * Creates a forward relationship batch request.
   *
   * @param requests forward requests
   */
  public ForwardRelationshipBatchRequest {
    requests = List.copyOf(requests);
    ResolverBounds.batchSize(requests.size());
  }
}
