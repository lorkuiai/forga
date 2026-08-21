package com.luokuiai.forga.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.List;
import org.junit.jupiter.api.Test;

class QueryConstraintModelTest {

  private static final QueryResource RESOURCE = new QueryResource("resource");

  private static final QueryResource RELATED = new QueryResource("related");

  private static final QueryField RESOURCE_ID = new QueryField(RESOURCE, "id");

  private static final QueryField RELATED_RESOURCE_ID = new QueryField(RELATED, "resource_id");

  private static final QueryField RELATED_SUBJECT = new QueryField(RELATED, "subject_id");

  private static final ResourceQueryMapping RESOURCE_MAPPING =
      ResourceQueryMapping.of(RESOURCE, List.of("id", "created_at"));

  private static final ResourceQueryMapping RELATED_MAPPING =
      ResourceQueryMapping.of(RELATED, List.of("resource_id", "subject_id", "rank", "relation"));

  @Test
  void modelsCorrelatedExistenceWithBoundParameter() {
    QueryParameter subject = new QueryParameter("subject", QueryValueType.STRING);
    PredicateConstraint predicate =
        QueryConstraint.predicate(RELATED_SUBJECT, PredicateOperator.EQUALS, subject);
    QueryJoin join =
        new QueryJoin(
            RESOURCE, RELATED, List.of(new QueryCorrelation(RESOURCE_ID, RELATED_RESOURCE_ID)));

    ExistsConstraint exists = new ExistsConstraint(RELATED, List.of(join), predicate);

    assertThat(exists.resource()).isEqualTo(RELATED);
    assertThat(exists.joins()).containsExactly(join);
    assertThat(exists.where()).isEqualTo(predicate);
  }

  @Test
  void composesBooleanConstraints() {
    PredicateConstraint first =
        QueryConstraint.predicate(
            RESOURCE_ID,
            PredicateOperator.EQUALS,
            new QueryParameter("resourceId", QueryValueType.OPAQUE));
    PredicateConstraint second =
        QueryConstraint.predicate(
            RELATED_SUBJECT,
            PredicateOperator.EQUALS,
            new QueryParameter("subject", QueryValueType.STRING));

    BooleanConstraint and = QueryConstraint.and(List.of(first, second));
    BooleanConstraint not = QueryConstraint.not(first);

    assertThat(and.operator()).isEqualTo(BooleanOperator.AND);
    assertThat(and.constraints()).containsExactly(first, second);
    assertThat(not.operator()).isEqualTo(BooleanOperator.NOT);
    assertThat(not.constraints()).containsExactly(first);
  }

  @Test
  void modelsAuthorizedRowsetListPlan() {
    QueryParameter subject = new QueryParameter("subject", QueryValueType.STRING);
    QueryConstraint where =
        QueryConstraint.predicate(
            RELATED_MAPPING.field("subject_id"), PredicateOperator.EQUALS, subject);
    AuthorizedListQuery query =
        QueryConstraintGenerator.authorizedRowset(
            RESOURCE_MAPPING,
            RELATED_MAPPING,
            "id",
            "resource_id",
            where,
            List.of(new QueryProjection(RELATED_MAPPING.field("relation"), "forga_relation")),
            List.of(
                new QueryOrdering(RELATED_MAPPING.field("rank"), QuerySortDirection.DESC),
                new QueryOrdering(RESOURCE_MAPPING.field("created_at"), QuerySortDirection.DESC)));

    assertThat(query.join().correlations())
        .containsExactly(
            new QueryCorrelation(
                RESOURCE_MAPPING.field("id"), RELATED_MAPPING.field("resource_id")));
    assertThat(query.projections()).extracting(QueryProjection::alias)
        .containsExactly("forga_relation");
    assertThat(query.orderings()).hasSize(2);
  }

  @Test
  void rejectsInvalidNamesAndArity() {
    assertThatIllegalArgumentException().isThrownBy(() -> new QueryResource("1bad"));
    assertThatIllegalArgumentException().isThrownBy(() -> new QueryField(RESOURCE, "bad name"));
    assertThatIllegalArgumentException().isThrownBy(() -> QueryConstraint.and(List.of()));
    assertThatIllegalArgumentException()
        .isThrownBy(() -> new BooleanConstraint(BooleanOperator.NOT, List.of()));
    assertThatIllegalArgumentException()
        .isThrownBy(() -> new QueryJoin(RESOURCE, RELATED, List.of()));
    assertThatIllegalArgumentException()
        .isThrownBy(
            () ->
                new AuthorizedRowsetJoin(
                    RESOURCE_MAPPING,
                    RELATED_MAPPING,
                    List.of(new QueryCorrelation(RELATED_SUBJECT, RESOURCE_ID))));
  }

  @Test
  void rejectsRequiredNulls() {
    assertThatNullPointerException().isThrownBy(() -> new QueryField(null, "id"));
    assertThatNullPointerException()
        .isThrownBy(() -> new QueryParameter("subject", null));
    assertThatNullPointerException()
        .isThrownBy(
            () -> QueryConstraint.predicate(
                RESOURCE_ID, null, new QueryFieldOperand(RESOURCE_ID)));
  }
}
