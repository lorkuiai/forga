package com.luokuiai.forga.resolver;

import java.util.List;

/**
 * Batch response for reverse relationship requests.
 *
 * @param responses immutable reverse responses
 */
public record ReverseRelationshipBatchResponse(List<ReverseRelationshipResponse> responses) {

  /**
   * Creates a reverse relationship batch response.
   *
   * @param responses reverse responses
   */
  public ReverseRelationshipBatchResponse {
    responses = List.copyOf(responses);
    ResolverBounds.batchSize(responses.size());
  }
}
