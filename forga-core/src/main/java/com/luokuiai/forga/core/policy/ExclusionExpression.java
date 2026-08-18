package com.luokuiai.forga.core.policy;

/**
 * Permission expression that removes matches from a base branch.
 *
 * @param base branch that can grant access
 * @param excluded branch that removes access when matched
 */
public record ExclusionExpression(PermissionExpression base, PermissionExpression excluded)
    implements PermissionExpression {

  /**
   * Creates an exclusion expression.
   *
   * @param base branch that can grant access
   * @param excluded branch that removes access when matched
   */
  public ExclusionExpression {
    base = ExpressionValidator.expression("base", base);
    excluded = ExpressionValidator.expression("excluded", excluded);
  }
}
