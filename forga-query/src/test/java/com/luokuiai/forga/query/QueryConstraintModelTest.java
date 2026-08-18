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
  void rejectsInvalidNamesAndArity() {
    assertThatIllegalArgumentException().isThrownBy(() -> new QueryResource("1bad"));
    assertThatIllegalArgumentException().isThrownBy(() -> new QueryField(RESOURCE, "bad name"));
    assertThatIllegalArgumentException().isThrownBy(() -> QueryConstraint.and(List.of()));
    assertThatIllegalArgumentException()
        .isThrownBy(() -> new BooleanConstraint(BooleanOperator.NOT, List.of()));
    assertThatIllegalArgumentException()
        .isThrownBy(() -> new QueryJoin(RESOURCE, RELATED, List.of()));
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
