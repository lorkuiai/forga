package com.luokuiai.forga.query;

import java.util.List;
import java.util.Objects;

/**
 * Boolean composition of query constraints.
 *
 * @param operator boolean operator
 * @param constraints child constraints
 */
public record BooleanConstraint(BooleanOperator operator, List<QueryConstraint> constraints)
    implements QueryConstraint {

  /**
   * Creates a boolean constraint.
   *
   * @param operator boolean operator
   * @param constraints child constraints
   */
  public BooleanConstraint {
    operator = Objects.requireNonNull(operator, "operator is required");
    constraints = List.copyOf(constraints);
    if (operator == BooleanOperator.NOT && constraints.size() != 1) {
      throw new IllegalArgumentException("NOT requires exactly one child");
    }
    if (operator != BooleanOperator.NOT && constraints.size() < 2) {
      throw new IllegalArgumentException(operator + " requires at least two children");
    }
  }
}
