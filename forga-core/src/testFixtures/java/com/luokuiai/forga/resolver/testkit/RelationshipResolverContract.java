package com.luokuiai.forga.resolver.testkit;

import static org.assertj.core.api.Assertions.assertThat;

import com.luokuiai.forga.resolver.AttributeResolutionBatchRequest;
import com.luokuiai.forga.resolver.AttributeResolutionBatchResponse;
import com.luokuiai.forga.resolver.ForwardRelationshipBatchRequest;
import com.luokuiai.forga.resolver.ForwardRelationshipBatchResponse;
import com.luokuiai.forga.resolver.RelationshipResolver;
import com.luokuiai.forga.resolver.ReverseRelationshipBatchRequest;
import com.luokuiai.forga.resolver.ReverseRelationshipBatchResponse;
import org.junit.jupiter.api.Test;

/**
 * Reusable contract tests for relationship resolvers.
 */
public abstract class RelationshipResolverContract {

  /**
   * Returns the resolver under test.
   *
   * @return resolver under test
   */
  protected abstract RelationshipResolver resolver();

  /**
   * Returns a forward batch expected to resolve at least one subject.
   *
   * @return forward batch request
   */
  protected abstract ForwardRelationshipBatchRequest forwardBatch();

  /**
   * Returns a reverse batch expected to resolve at least one object.
   *
   * @return reverse batch request
   */
  protected abstract ReverseRelationshipBatchRequest reverseBatch();

  /**
   * Returns an attribute batch expected to resolve every requested attribute.
   *
   * @return attribute batch request
   */
  protected abstract AttributeResolutionBatchRequest attributeBatch();

  @Test
  final void forwardResponsesAreBoundedAndCarryConsistency() {
    ForwardRelationshipBatchResponse response = resolver().resolveForward(forwardBatch());

    assertThat(response.responses()).hasSameSizeAs(forwardBatch().requests());
    response.responses()
        .forEach(
            item -> {
              assertThat(item.subjects()).hasSizeLessThanOrEqualTo(item.request().limit());
              assertThat(item.consistency()).isNotNull();
            });
  }

  @Test
  final void reverseResponsesAreBoundedAndCarryStableCursorState() {
    ReverseRelationshipBatchResponse response = resolver().resolveReverse(reverseBatch());

    assertThat(response.responses()).hasSameSizeAs(reverseBatch().requests());
    response.responses()
        .forEach(
            item -> {
              assertThat(item.objects()).hasSizeLessThanOrEqualTo(item.request().limit());
              item.nextCursor().ifPresent(cursor -> assertThat(cursor.value()).isNotBlank());
              assertThat(item.consistency()).isNotNull();
            });
  }

  @Test
  final void attributeResponsesMatchRequestedAttributes() {
    AttributeResolutionBatchResponse response = resolver().resolveAttributes(attributeBatch());

    assertThat(response.responses()).hasSameSizeAs(attributeBatch().requests());
    response.responses()
        .forEach(
            item -> {
              assertThat(item.attributes())
                  .extracting(attribute -> attribute.attribute())
                  .containsExactlyInAnyOrderElementsOf(item.request().attributes());
              assertThat(item.consistency()).isNotNull();
            });
  }
}
