package com.luokuiai.forga.resolver;

import java.util.List;

/**
 * Batch response for forward relationship requests.
 *
 * @param responses immutable forward responses
 */
public record ForwardRelationshipBatchResponse(List<ForwardRelationshipResponse> responses) {

  /**
   * Creates a forward relationship batch response.
   *
   * @param responses forward responses
   */
  public ForwardRelationshipBatchResponse {
    responses = List.copyOf(responses);
    ResolverBounds.batchSize(responses.size());
  }
}
