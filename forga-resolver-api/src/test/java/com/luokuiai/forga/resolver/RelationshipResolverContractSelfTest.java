package com.luokuiai.forga.resolver;

import com.luokuiai.forga.core.model.AttributeRef;
import com.luokuiai.forga.core.model.ObjectRef;
import com.luokuiai.forga.core.model.RelationRef;
import com.luokuiai.forga.core.model.SubjectRef;
import com.luokuiai.forga.resolver.testkit.RelationshipResolverContract;
import java.util.List;
import java.util.Set;

class RelationshipResolverContractSelfTest extends RelationshipResolverContract {

  private static final RelationRef VIEWER = new RelationRef("viewer");

  private static final AttributeRef STATUS = new AttributeRef("status");

  private static final ObjectRef DOCUMENT = new ObjectRef("document", "doc-1");

  private static final DirectSubject PRINCIPAL =
      new DirectSubject(new SubjectRef("principal", "subject-1"));

  private final RelationshipResolver resolver = new SampleResolver();

  @Override
  protected RelationshipResolver resolver() {
    return resolver;
  }

  @Override
  protected ForwardRelationshipBatchRequest forwardBatch() {
    return new ForwardRelationshipBatchRequest(
        List.of(new ForwardRelationshipRequest(DOCUMENT, VIEWER, 10)));
  }

  @Override
  protected ReverseRelationshipBatchRequest reverseBatch() {
    return new ReverseRelationshipBatchRequest(
        List.of(new ReverseRelationshipRequest("document", VIEWER, PRINCIPAL, 10)));
  }

  @Override
  protected AttributeResolutionBatchRequest attributeBatch() {
    return new AttributeResolutionBatchRequest(
        List.of(new AttributeResolutionRequest(DOCUMENT, List.of(STATUS))));
  }

  private static final class SampleResolver implements RelationshipResolver {

    private final ResolverDescriptor descriptor =
        new ResolverDescriptor("sample", Set.of(VIEWER), Set.of(VIEWER), Set.of(STATUS));

    @Override
    public ResolverDescriptor descriptor() {
      return descriptor;
    }

    @Override
    public ForwardRelationshipBatchResponse resolveForward(
        ForwardRelationshipBatchRequest request) {
      return new ForwardRelationshipBatchResponse(
          request.requests().stream()
              .map(item -> new ForwardRelationshipResponse(item, List.of(PRINCIPAL)))
              .toList());
    }

    @Override
    public ReverseRelationshipBatchResponse resolveReverse(
        ReverseRelationshipBatchRequest request) {
      return new ReverseRelationshipBatchResponse(
          request.requests().stream()
              .map(item -> new ReverseRelationshipResponse(item, List.of(DOCUMENT)))
              .toList());
    }

    @Override
    public AttributeResolutionBatchResponse resolveAttributes(
        AttributeResolutionBatchRequest request) {
      return new AttributeResolutionBatchResponse(
          request.requests().stream()
              .map(
                  item ->
                      new AttributeResolutionResponse(
                          item, List.of(new ResolvedAttribute(STATUS, "active"))))
              .toList());
    }
  }
}
