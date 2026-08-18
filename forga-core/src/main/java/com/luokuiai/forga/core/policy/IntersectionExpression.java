package com.luokuiai.forga.core.policy;

import java.util.List;

/**
 * Permission expression that matches when every branch matches.
 *
 * @param expressions immutable branches
 */
public record IntersectionExpression(List<PermissionExpression> expressions)
    implements PermissionExpression {

  /**
   * Creates an intersection expression.
   *
   * @param expressions branches to evaluate
   */
  public IntersectionExpression {
    expressions = ExpressionValidator.branches(expressions);
  }
}
