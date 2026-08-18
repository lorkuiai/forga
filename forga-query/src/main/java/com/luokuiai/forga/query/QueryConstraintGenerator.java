package com.luokuiai.forga.query;

import java.util.List;
import java.util.Objects;

/**
 * Generates set-oriented typed constraints from allowlisted resource mappings.
 */
public final class QueryConstraintGenerator {

  private QueryConstraintGenerator() {
  }

  /**
   * Creates a correlated existence constraint.
   *
   * @param outer outer resource mapping
   * @param inner inner resource mapping
   * @param outerField correlated outer field name
   * @param innerField correlated inner field name
   * @param predicateField inner predicate field name
   * @param parameter bound predicate parameter
   * @return existence constraint
   */
  public static ExistsConstraint correlatedExists(
      ResourceQueryMapping outer,
      ResourceQueryMapping inner,
      String outerField,
      String innerField,
      String predicateField,
      QueryParameter parameter) {
    Objects.requireNonNull(outer, "outer is required");
    Objects.requireNonNull(inner, "inner is required");
    Objects.requireNonNull(parameter, "parameter is required");
    QueryJoin join =
        new QueryJoin(
            outer.resource(),
            inner.resource(),
            List.of(new QueryCorrelation(outer.field(outerField), inner.field(innerField))));
    PredicateConstraint predicate =
        QueryConstraint.predicate(
            inner.field(predicateField), PredicateOperator.EQUALS, parameter);
    return new ExistsConstraint(inner.resource(), List.of(join), predicate);
  }
}
