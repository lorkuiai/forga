package com.luokuiai.forga.core.policy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import com.luokuiai.forga.core.model.CaveatRef;
import com.luokuiai.forga.core.model.PermissionRef;
import com.luokuiai.forga.core.model.RelationRef;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class PolicyCompilerTest {

  @Test
  void compilesPolicyWithStableFingerprint() {
    PolicyDefinition first =
        new PolicyDefinition(
            Map.of(
                new PermissionRef("view"),
                PermissionExpression.caveat(
                    PermissionExpression.union(
                        List.of(
                            PermissionExpression.relation(new RelationRef("viewer")),
                            PermissionExpression.traversal(
                                new RelationRef("parent"),
                                PermissionExpression.relation(new RelationRef("owner"))))),
                    new CaveatRef("active"))));
    PolicyDefinition reordered =
        new PolicyDefinition(new LinkedHashMap<>(first.permissions()));

    ResolverCapabilities capabilities =
        ResolverCapabilities.of(
            List.of(
                new RelationRef("viewer"), new RelationRef("parent"), new RelationRef("owner")),
            List.of(new CaveatRef("active")));

    CompiledPolicy compiled = PolicyCompiler.compile(first, capabilities);
    CompiledPolicy compiledAgain = PolicyCompiler.compile(reordered, capabilities);

    assertThat(compiled.definition()).isEqualTo(first);
    assertThat(compiled.fingerprint()).startsWith("sha256:");
    assertThat(compiled.fingerprint()).hasSize(71);
    assertThat(compiledAgain.fingerprint()).isEqualTo(compiled.fingerprint());
  }

  @Test
  void fingerprintDoesNotDependOnPermissionMapOrder() {
    PermissionExpression viewer = PermissionExpression.relation(new RelationRef("viewer"));
    PermissionExpression editor = PermissionExpression.relation(new RelationRef("editor"));
    Map<PermissionRef, PermissionExpression> first = new LinkedHashMap<>();
    first.put(new PermissionRef("view"), viewer);
    first.put(new PermissionRef("edit"), editor);
    Map<PermissionRef, PermissionExpression> second = new LinkedHashMap<>();
    second.put(new PermissionRef("edit"), editor);
    second.put(new PermissionRef("view"), viewer);

    ResolverCapabilities capabilities =
        ResolverCapabilities.of(
            List.of(new RelationRef("viewer"), new RelationRef("editor")), List.of());

    assertThat(PolicyCompiler.compile(new PolicyDefinition(first), capabilities).fingerprint())
        .isEqualTo(
            PolicyCompiler.compile(new PolicyDefinition(second), capabilities).fingerprint());
  }

  @Test
  void rejectsMissingRelationCapability() {
    PolicyDefinition definition =
        new PolicyDefinition(
            Map.of(
                new PermissionRef("view"),
                PermissionExpression.relation(new RelationRef("viewer"))));

    assertThatExceptionOfType(PolicyValidationException.class)
        .isThrownBy(
            () -> PolicyCompiler.compile(definition, ResolverCapabilities.of(List.of(), List.of())))
        .withMessageContaining("unsupported relation");
  }

  @Test
  void rejectsMissingCaveatCapability() {
    PolicyDefinition definition =
        new PolicyDefinition(
            Map.of(
                new PermissionRef("view"),
                PermissionExpression.caveat(
                    PermissionExpression.relation(new RelationRef("viewer")),
                    new CaveatRef("active"))));

    ResolverCapabilities capabilities =
        ResolverCapabilities.of(List.of(new RelationRef("viewer")), List.of());

    assertThatExceptionOfType(PolicyValidationException.class)
        .isThrownBy(() -> PolicyCompiler.compile(definition, capabilities))
        .withMessageContaining("unsupported caveat");
  }

  @Test
  void rejectsEmptyPolicyDefinitions() {
    assertThatIllegalArgumentException().isThrownBy(() -> new PolicyDefinition(Map.of()));
  }

  @Test
  void rejectsNullCompileInputs() {
    ResolverCapabilities capabilities = ResolverCapabilities.of(List.of(), List.of());
    PolicyDefinition definition =
        new PolicyDefinition(
            Map.of(
                new PermissionRef("view"),
                PermissionExpression.relation(new RelationRef("viewer"))));

    assertThatNullPointerException().isThrownBy(() -> PolicyCompiler.compile(null, capabilities));
    assertThatNullPointerException().isThrownBy(() -> PolicyCompiler.compile(definition, null));
  }
}
