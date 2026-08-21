package com.luokuiai.forga.resolver;

import java.util.List;

/**
 * Batch response for attribute resolution requests.
 *
 * @param responses immutable attribute responses
 */
public record AttributeResolutionBatchResponse(List<AttributeResolutionResponse> responses) {

  /**
   * Creates an attribute resolution batch response.
   *
   * @param responses attribute responses
   */
  public AttributeResolutionBatchResponse {
    responses = List.copyOf(responses);
    ResolverBounds.batchSize(responses.size());
  }
}
