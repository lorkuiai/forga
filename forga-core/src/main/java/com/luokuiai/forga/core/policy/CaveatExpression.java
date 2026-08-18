package com.luokuiai.forga.core.policy;

import com.luokuiai.forga.core.model.CaveatRef;

/**
 * Permission expression guarded by a caveat.
 *
 * @param expression guarded expression
 * @param caveat caveat to evaluate
 */
public record CaveatExpression(PermissionExpression expression, CaveatRef caveat)
    implements PermissionExpression {

  /**
   * Creates a caveat expression.
   *
   * @param expression guarded expression
   * @param caveat caveat to evaluate
   */
  public CaveatExpression {
    expression = ExpressionValidator.expression("expression", expression);
    caveat = ExpressionValidator.value("caveat", caveat);
  }
}
