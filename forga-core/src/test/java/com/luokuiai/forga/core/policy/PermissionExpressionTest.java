package com.luokuiai.forga.core.policy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import com.luokuiai.forga.core.model.CaveatRef;
import com.luokuiai.forga.core.model.RelationRef;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class PermissionExpressionTest {

  @Test
  void createsComposableExpressionTree() {
    RelationExpression viewer = PermissionExpression.relation(new RelationRef("viewer"));
    RelationExpression editor = PermissionExpression.relation(new RelationRef("editor"));

    PermissionExpression expression =
        PermissionExpression.caveat(
            PermissionExpression.exclusion(
                PermissionExpression.union(List.of(viewer, editor)),
                PermissionExpression.traversal(
                    new RelationRef("blocked_parent"),
                    PermissionExpression.relation(new RelationRef("blocked")))),
            new CaveatRef("within-hours"));

    assertThat(expression).isInstanceOf(CaveatExpression.class);
    CaveatExpression caveat = (CaveatExpression) expression;
    assertThat(caveat.caveat()).isEqualTo(new CaveatRef("within-hours"));
    assertThat(caveat.expression()).isInstanceOf(ExclusionExpression.class);
  }

  @Test
  void copiesBranchCollections() {
    RelationExpression viewer = PermissionExpression.relation(new RelationRef("viewer"));
    RelationExpression editor = PermissionExpression.relation(new RelationRef("editor"));
    List<PermissionExpression> branches = new ArrayList<>(List.of(viewer, editor));

    UnionExpression expression = PermissionExpression.union(branches);
    branches.clear();

    assertThat(expression.expressions()).containsExactly(viewer, editor);
    assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(() -> expression.expressions().clear());
  }

  @Test
  void createsIntersectionExpressions() {
    RelationExpression viewer = PermissionExpression.relation(new RelationRef("viewer"));
    RelationExpression active = PermissionExpression.relation(new RelationRef("active"));

    IntersectionExpression expression = PermissionExpression.intersection(List.of(viewer, active));

    assertThat(expression.expressions()).containsExactly(viewer, active);
  }

  @Test
  void rejectsTooFewBranches() {
    RelationExpression viewer = PermissionExpression.relation(new RelationRef("viewer"));

    assertThatIllegalArgumentException()
        .isThrownBy(() -> PermissionExpression.union(List.of(viewer)));
    assertThatIllegalArgumentException()
        .isThrownBy(() -> PermissionExpression.intersection(List.of()));
  }

  @Test
  void rejectsNullDependencies() {
    RelationExpression viewer = PermissionExpression.relation(new RelationRef("viewer"));

    assertThatNullPointerException().isThrownBy(() -> PermissionExpression.relation(null));
    assertThatNullPointerException().isThrownBy(() -> PermissionExpression.union(null));
    assertThatNullPointerException()
        .isThrownBy(() -> PermissionExpression.union(List.of(viewer, null)));
    assertThatNullPointerException()
        .isThrownBy(() -> PermissionExpression.exclusion(viewer, null));
    assertThatNullPointerException()
        .isThrownBy(() -> PermissionExpression.traversal(null, viewer));
    assertThatNullPointerException().isThrownBy(() -> PermissionExpression.caveat(null, null));
  }
}
