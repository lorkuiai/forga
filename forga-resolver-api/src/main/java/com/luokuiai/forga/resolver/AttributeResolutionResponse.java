package com.luokuiai.forga.resolver;

import java.util.List;
import java.util.Objects;

/**
 * Response containing resolved attributes.
 *
 * @param request request that produced the response
 * @param attributes immutable resolved attributes
 * @param consistency returned consistency context
 */
public record AttributeResolutionResponse(
    AttributeResolutionRequest request,
    List<ResolvedAttribute> attributes,
    ConsistencyContext consistency) {

  /**
   * Creates an attribute resolution response with request consistency context.
   *
   * @param request request that produced the response
   * @param attributes resolved attributes
   */
  public AttributeResolutionResponse(
      AttributeResolutionRequest request, List<ResolvedAttribute> attributes) {
    this(request, attributes, request.context().consistency());
  }

  /**
   * Creates an attribute resolution response.
   *
   * @param request request that produced the response
   * @param attributes resolved attributes
   * @param consistency returned consistency context
   */
  public AttributeResolutionResponse {
    request = Objects.requireNonNull(request, "request is required");
    attributes = List.copyOf(attributes);
    consistency = Objects.requireNonNull(consistency, "consistency is required");
  }
}
