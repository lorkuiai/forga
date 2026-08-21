package com.luokuiai.forga.scope;

import static org.assertj.core.api.Assertions.assertThat;

import com.luokuiai.forga.query.BooleanConstraint;
import com.luokuiai.forga.query.BooleanOperator;
import com.luokuiai.forga.query.PredicateConstraint;
import com.luokuiai.forga.query.PredicateOperator;
import com.luokuiai.forga.query.QueryConstraint;
import com.luokuiai.forga.query.QueryParameter;
import com.luokuiai.forga.query.QueryResource;
import com.luokuiai.forga.query.QueryValueType;
import com.luokuiai.forga.query.ResourceQueryMapping;
import java.util.List;
import org.junit.jupiter.api.Test;

class ScopeQueryConstraintsTest {

  @Test
  void generatesActiveScopePredicatesWithBoundParameters() {
    QueryResource resource = new QueryResource("documents");
    ResourceQueryMapping mapping =
        ResourceQueryMapping.of(resource, List.of("scope_type", "scope_id", "title"));

    QueryConstraint constraint =
        ScopeQueryConstraints.activeScope(mapping, "scope_type", "scope_id");

    BooleanConstraint booleanConstraint = (BooleanConstraint) constraint;
    assertThat(booleanConstraint.operator()).isEqualTo(BooleanOperator.AND);
    assertThat(booleanConstraint.constraints()).hasSize(2);
    PredicateConstraint typePredicate =
        (PredicateConstraint) booleanConstraint.constraints().get(0);
    PredicateConstraint idPredicate =
        (PredicateConstraint) booleanConstraint.constraints().get(1);
    assertThat(typePredicate.operator()).isEqualTo(PredicateOperator.EQUALS);
    assertThat(typePredicate.right())
        .isEqualTo(
            new QueryParameter(
                ScopeQueryConstraints.SCOPE_TYPE_PARAMETER, QueryValueType.STRING));
    assertThat(idPredicate.right())
        .isEqualTo(
            new QueryParameter(ScopeQueryConstraints.SCOPE_ID_PARAMETER, QueryValueType.STRING));
  }

  @Test
  void combinesActiveScopeWithExplicitGrantConstraint() {
    QueryResource resource = new QueryResource("documents");
    ResourceQueryMapping mapping =
        ResourceQueryMapping.of(resource, List.of("scope_type", "scope_id", "grant_id"));
    QueryConstraint granted =
        QueryConstraint.predicate(
            mapping.field("grant_id"),
            PredicateOperator.EQUALS,
            new QueryParameter("grant", QueryValueType.STRING));

    QueryConstraint constraint =
        ScopeQueryConstraints.activeOrGranted(
            mapping, "scope_type", "scope_id", granted);

    BooleanConstraint booleanConstraint = (BooleanConstraint) constraint;
    assertThat(booleanConstraint.operator()).isEqualTo(BooleanOperator.OR);
    assertThat(booleanConstraint.constraints()).hasSize(2);
    assertThat(booleanConstraint.constraints().get(0)).isInstanceOf(BooleanConstraint.class);
    assertThat(booleanConstraint.constraints().get(1)).isSameAs(granted);
  }

  @Test
  void exposesParameterValuesForActiveScope() {
    ActiveScope activeScope = new ActiveScope(new ScopeRef("workspace", "alpha"));

    assertThat(ScopeQueryConstraints.parameters(activeScope))
        .containsEntry(ScopeQueryConstraints.SCOPE_TYPE_PARAMETER, "workspace")
        .containsEntry(ScopeQueryConstraints.SCOPE_ID_PARAMETER, "alpha");
  }
}
