package com.luokuiai.forga.query;

import java.util.Collection;
import java.util.List;

/**
 * Typed authorization query constraint.
 */
public sealed interface QueryConstraint
    permits PredicateConstraint, BooleanConstraint, ExistsConstraint {

  /**
   * Creates a predicate constraint.
   *
   * @param left left field
   * @param operator predicate operator
   * @param right right operand
   * @return predicate constraint
   */
  static PredicateConstraint predicate(
      QueryField left, PredicateOperator operator, QueryOperand right) {
    return new PredicateConstraint(left, operator, right);
  }

  /**
   * Creates an AND constraint.
   *
   * @param constraints child constraints
   * @return boolean constraint
   */
  static BooleanConstraint and(Collection<? extends QueryConstraint> constraints) {
    return new BooleanConstraint(BooleanOperator.AND, List.copyOf(constraints));
  }

  /**
   * Creates an OR constraint.
   *
   * @param constraints child constraints
   * @return boolean constraint
   */
  static BooleanConstraint or(Collection<? extends QueryConstraint> constraints) {
    return new BooleanConstraint(BooleanOperator.OR, List.copyOf(constraints));
  }

  /**
   * Creates a NOT constraint.
   *
   * @param constraint child constraint
   * @return boolean constraint
   */
  static BooleanConstraint not(QueryConstraint constraint) {
    return new BooleanConstraint(BooleanOperator.NOT, List.of(constraint));
  }
}
