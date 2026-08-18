package com.luokuiai.forga.core.policy;

import java.util.List;
import java.util.Objects;

final class ExpressionValidator {

  private ExpressionValidator() {
  }

  static PermissionExpression expression(String field, PermissionExpression expression) {
    return Objects.requireNonNull(expression, field + " is required");
  }

  static <T> T value(String field, T value) {
    return Objects.requireNonNull(value, field + " is required");
  }

  static List<PermissionExpression> branches(List<PermissionExpression> expressions) {
    Objects.requireNonNull(expressions, "expressions are required");
    if (expressions.size() < 2) {
      throw new IllegalArgumentException("at least two expressions are required");
    }
    expressions.forEach(expression -> expression("expression", expression));
    return List.copyOf(expressions);
  }
}
