package com.luokuiai.forga.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.List;
import org.junit.jupiter.api.Test;

class QueryConstraintGeneratorTest {

  private static final QueryResource OUTER = new QueryResource("outer");

  private static final QueryResource INNER = new QueryResource("inner");

  private static final ResourceQueryMapping OUTER_MAPPING =
      ResourceQueryMapping.of(OUTER, List.of("id", "state"));

  private static final ResourceQueryMapping INNER_MAPPING =
      ResourceQueryMapping.of(INNER, List.of("outer_id", "subject_id", "kind"));

  @Test
  void generatesCorrelatedExistenceConstraintWithBoundParameter() {
    QueryParameter subject = new QueryParameter("subject", QueryValueType.STRING);

    ExistsConstraint constraint =
        QueryConstraintGenerator.correlatedExists(
            OUTER_MAPPING, INNER_MAPPING, "id", "outer_id", "subject_id", subject);

    assertThat(constraint.resource()).isEqualTo(INNER);
    assertThat(constraint.joins()).hasSize(1);
    assertThat(constraint.joins().get(0).correlations())
        .containsExactly(
            new QueryCorrelation(
                new QueryField(OUTER, "id"), new QueryField(INNER, "outer_id")));
    assertThat(((PredicateConstraint) constraint.where()).right()).isEqualTo(subject);
  }

  @Test
  void generatedConstraintCanBeComposedWithBooleanNodes() {
    ExistsConstraint exists =
        QueryConstraintGenerator.correlatedExists(
            OUTER_MAPPING,
            INNER_MAPPING,
            "id",
            "outer_id",
            "subject_id",
            new QueryParameter("subject", QueryValueType.STRING));
    PredicateConstraint state =
        QueryConstraint.predicate(
            OUTER_MAPPING.field("state"),
            PredicateOperator.EQUALS,
            new QueryParameter("state", QueryValueType.STRING));

    BooleanConstraint constraint = QueryConstraint.and(List.of(exists, state));

    assertThat(constraint.operator()).isEqualTo(BooleanOperator.AND);
    assertThat(constraint.constraints()).containsExactly(exists, state);
  }

  @Test
  void rejectsUnknownMappedFieldsBeforeTranslation() {
    assertThatExceptionOfType(UnknownQueryFieldException.class)
        .isThrownBy(
            () ->
                QueryConstraintGenerator.correlatedExists(
                    OUTER_MAPPING,
                    INNER_MAPPING,
                    "missing",
                    "outer_id",
                    "subject_id",
                    new QueryParameter("subject", QueryValueType.STRING)));
  }

  @Test
  void rejectsInvalidMappings() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> ResourceQueryMapping.of(OUTER, List.of()));
    assertThatIllegalArgumentException()
        .isThrownBy(() -> ResourceQueryMapping.of(OUTER, List.of("bad field")));
  }
}
