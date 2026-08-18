package com.luokuiai.forga.core.policy;

import com.luokuiai.forga.core.model.RelationRef;

/**
 * Permission expression backed by a direct relation.
 *
 * @param relation relation to inspect
 */
public record RelationExpression(RelationRef relation) implements PermissionExpression {

  /**
   * Creates a relation expression.
   *
   * @param relation relation to inspect
   */
  public RelationExpression {
    relation = ExpressionValidator.value("relation", relation);
  }
}
