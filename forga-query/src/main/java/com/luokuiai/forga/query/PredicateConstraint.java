package com.luokuiai.forga.query;

import java.util.Objects;

/**
 * Typed field predicate.
 *
 * @param left left field
 * @param operator predicate operator
 * @param right right operand
 */
public record PredicateConstraint(
    QueryField left, PredicateOperator operator, QueryOperand right) implements QueryConstraint {

  /**
   * Creates a predicate constraint.
   *
   * @param left left field
   * @param operator predicate operator
   * @param right right operand
   */
  public PredicateConstraint {
    left = Objects.requireNonNull(left, "left is required");
    operator = Objects.requireNonNull(operator, "operator is required");
    right = Objects.requireNonNull(right, "right is required");
  }
}
