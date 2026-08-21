package com.luokuiai.forga.resolver;

/**
 * Resolver capable of answering relationship and attribute requests.
 */
public interface RelationshipResolver {

  /**
   * Returns declared resolver capabilities.
   *
   * @return resolver descriptor
   */
  ResolverDescriptor descriptor();

  /**
   * Resolves forward relationship requests.
   *
   * @param request batch request
   * @return batch response
   */
  ForwardRelationshipBatchResponse resolveForward(ForwardRelationshipBatchRequest request);

  /**
   * Resolves reverse relationship requests.
   *
   * @param request batch request
   * @return batch response
   */
  ReverseRelationshipBatchResponse resolveReverse(ReverseRelationshipBatchRequest request);

  /**
   * Resolves attribute requests.
   *
   * @param request batch request
   * @return batch response
   */
  AttributeResolutionBatchResponse resolveAttributes(AttributeResolutionBatchRequest request);
}
