package com.luokuiai.forga.core.policy;

import java.util.List;

/**
 * Permission expression that matches when any branch matches.
 *
 * @param expressions immutable branches
 */
public record UnionExpression(List<PermissionExpression> expressions)
    implements PermissionExpression {

  /**
   * Creates a union expression.
   *
   * @param expressions branches to evaluate
   */
  public UnionExpression {
    expressions = ExpressionValidator.branches(expressions);
  }
}
