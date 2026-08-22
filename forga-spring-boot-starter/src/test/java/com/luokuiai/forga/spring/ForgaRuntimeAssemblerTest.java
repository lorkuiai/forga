package com.luokuiai.forga.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.luokuiai.forga.core.eval.EvaluationLimits;
import com.luokuiai.forga.core.model.AttributeRef;
import com.luokuiai.forga.core.model.PermissionRef;
import com.luokuiai.forga.core.model.RelationRef;
import com.luokuiai.forga.core.model.SubjectRef;
import com.luokuiai.forga.core.policy.CompiledPolicy;
import com.luokuiai.forga.core.policy.PermissionExpression;
import com.luokuiai.forga.core.policy.PolicyCompiler;
import com.luokuiai.forga.core.policy.PolicyDefinition;
import com.luokuiai.forga.core.policy.ResolverCapabilities;
import com.luokuiai.forga.resolver.AttributeResolutionBatchRequest;
import com.luokuiai.forga.resolver.AttributeResolutionBatchResponse;
import com.luokuiai.forga.resolver.ForwardRelationshipBatchRequest;
import com.luokuiai.forga.resolver.ForwardRelationshipBatchResponse;
import com.luokuiai.forga.resolver.RelationshipResolver;
import com.luokuiai.forga.resolver.ResolverDescriptor;
import com.luokuiai.forga.resolver.ResolverRegistry;
import com.luokuiai.forga.resolver.ReverseRelationshipBatchRequest;
import com.luokuiai.forga.resolver.ReverseRelationshipBatchResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ForgaRuntimeAssemblerTest {

  private static final RelationRef VIEWER = new RelationRef("viewer");

  private static final PermissionRef VIEW = new PermissionRef("view");

  @Test
  void assemblyRequiresPolicyAndResolvers() {
    assertThatExceptionOfType(ForgaRuntimeException.class)
        .isThrownBy(
            () ->
                ForgaRuntimeAssembler.assemble(
                    null, null, EvaluationLimits.defaults(), Map.of()))
        .withMessageContaining("policy");
    assertThatExceptionOfType(ForgaRuntimeException.class)
        .isThrownBy(
            () ->
                ForgaRuntimeAssembler.assemble(
                    policy(), null, EvaluationLimits.defaults(), Map.of()))
        .withMessageContaining("resolver registry");
  }

  @Test
  void assemblyRequiresEvaluationLimits() {
    ResolverRegistry resolvers = new ResolverRegistry(List.of());

    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(() -> ForgaRuntimeAssembler.assemble(policy(), resolvers, null, Map.of()))
        .withMessage("limits are required");
  }

  @Test
  void assemblyRequiresReverseCapability() {
    ResolverRegistry resolvers =
        new ResolverRegistry(List.of(new TestResolver(Set.of(VIEWER), Set.of())));

    assertThatExceptionOfType(ForgaRuntimeException.class)
        .isThrownBy(
            () ->
                ForgaRuntimeAssembler.assemble(
                    policy(), resolvers, EvaluationLimits.defaults(), Map.of()))
        .withMessageContaining("missing reverse resolver");
  }

  @Test
  void assemblyBuildsComponentsWhenCapabilitiesMatch() {
    ResolverRegistry resolvers =
        new ResolverRegistry(List.of(new TestResolver(Set.of(VIEWER), Set.of(VIEWER))));

    EvaluationLimits limits = EvaluationLimits.defaults();
    ForgaRuntimeComponents components =
        ForgaRuntimeAssembler.assemble(policy(), resolvers, limits, Map.of());

    assertThat(components.limits()).isSameAs(limits);
  }

  @Test
  void requestScopeClearsContextAfterException() {
    SubjectRef subject = new SubjectRef("principal", "alice");
    ForgaRequestContext context =
        new ForgaRequestContext(subject, Map.of(), Optional.empty(), Optional.empty());

    try {
      try (ForgaRequestScope ignored = ForgaRequestScope.open(context)) {
        assertThat(ignored).isNotNull();
        assertThat(ForgaRequestScope.current()).contains(context);
        throw new IllegalStateException("downstream failure");
      }
    } catch (IllegalStateException exception) {
      assertThat(exception).hasMessage("downstream failure");
    }

    assertThat(ForgaRequestScope.current()).isEmpty();
  }

  private static CompiledPolicy policy() {
    return PolicyCompiler.compile(
        new PolicyDefinition(Map.of(VIEW, PermissionExpression.relation(VIEWER))),
        ResolverCapabilities.of(List.of(VIEWER), List.of()));
  }

  private record TestResolver(Set<RelationRef> forward, Set<RelationRef> reverse)
      implements RelationshipResolver {

    @Override
    public ResolverDescriptor descriptor() {
      return new ResolverDescriptor("test", forward, reverse, Set.<AttributeRef>of());
    }

    @Override
    public ForwardRelationshipBatchResponse resolveForward(
        ForwardRelationshipBatchRequest request) {
      return null;
    }

    @Override
    public ReverseRelationshipBatchResponse resolveReverse(
        ReverseRelationshipBatchRequest request) {
      return null;
    }

    @Override
    public AttributeResolutionBatchResponse resolveAttributes(
        AttributeResolutionBatchRequest request) {
      return null;
    }
  }
}
