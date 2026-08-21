package com.luokuiai.forga.resolver;

import java.util.List;

/**
 * Bounded batch of attribute resolution requests.
 *
 * @param requests immutable requests
 */
public record AttributeResolutionBatchRequest(List<AttributeResolutionRequest> requests) {

  /**
   * Creates an attribute resolution batch request.
   *
   * @param requests attribute requests
   */
  public AttributeResolutionBatchRequest {
    requests = List.copyOf(requests);
    ResolverBounds.batchSize(requests.size());
  }
}
