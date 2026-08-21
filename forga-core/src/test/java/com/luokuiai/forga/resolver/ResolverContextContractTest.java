package com.luokuiai.forga.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import com.luokuiai.forga.core.model.AttributeRef;
import com.luokuiai.forga.core.model.ConsistencyToken;
import com.luokuiai.forga.core.model.ObjectRef;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ResolverContextContractTest {

  @Test
  void createsResolverContextWithConsistencyAndDeadline() {
    ConsistencyContext consistency = ConsistencyContext.of(new ConsistencyToken("snapshot-1"));
    ResolverDeadline deadline = new ResolverDeadline(Instant.parse("2026-08-18T00:00:00Z"));

    ResolverContext context = new ResolverContext(consistency, Optional.of(deadline));

    assertThat(context.consistency()).isEqualTo(consistency);
    assertThat(context.deadline()).contains(deadline);
  }

  @Test
  void createsOpaquePageCursors() {
    assertThat(new PageCursor(" cursor-1 ").value()).isEqualTo("cursor-1");
  }

  @Test
  void resolvesAttributesWithConsistency() {
    AttributeResolutionRequest request =
        new AttributeResolutionRequest(
            new ObjectRef("document", "doc-1"), List.of(new AttributeRef("status")));
    ResolvedAttribute attribute = new ResolvedAttribute(new AttributeRef("status"), "active");

    AttributeResolutionResponse response =
        new AttributeResolutionResponse(
            request, List.of(attribute), ConsistencyContext.of(new ConsistencyToken("snapshot-1")));

    assertThat(response.request()).isEqualTo(request);
    assertThat(response.attributes()).containsExactly(attribute);
    assertThat(response.consistency().token()).contains(new ConsistencyToken("snapshot-1"));
  }

  @Test
  void createsStructuredFailures() {
    ResolverFailure failure =
        new ResolverFailure(ResolverFailureReason.TIMEOUT, " deadline exceeded ");

    assertThat(failure.reason()).isEqualTo(ResolverFailureReason.TIMEOUT);
    assertThat(failure.message()).isEqualTo("deadline exceeded");
  }

  @Test
  void rejectsInvalidContextValues() {
    assertThatNullPointerException().isThrownBy(() -> new ResolverDeadline(null));
    assertThatIllegalArgumentException().isThrownBy(() -> new PageCursor(" "));
    assertThatIllegalArgumentException()
        .isThrownBy(
            () ->
                new AttributeResolutionRequest(
                    new ObjectRef("document", "doc-1"), List.of()));
    assertThatNullPointerException()
        .isThrownBy(() -> new ResolvedAttribute(null, "active"));
    assertThatIllegalArgumentException()
        .isThrownBy(() -> new ResolverFailure(ResolverFailureReason.TIMEOUT, " "));
  }
}
