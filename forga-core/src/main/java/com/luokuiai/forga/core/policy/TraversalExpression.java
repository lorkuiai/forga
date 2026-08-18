package com.luokuiai.forga.core.policy;

import com.luokuiai.forga.core.model.RelationRef;
import java.util.Optional;

/**
 * Permission expression that follows a relation before evaluating another expression.
 *
 * @param relation relation to traverse
 * @param objectType optional caller-defined object type reached by the traversal
 * @param expression expression evaluated after traversal
 */
public record TraversalExpression(
    RelationRef relation, Optional<String> objectType, PermissionExpression expression)
    implements PermissionExpression {

  /**
   * Creates a traversal expression without a declared object type.
   *
   * @param relation relation to traverse
   * @param expression expression evaluated after traversal
   */
  public TraversalExpression(RelationRef relation, PermissionExpression expression) {
    this(relation, Optional.empty(), expression);
  }

  /**
   * Creates a traversal expression with a declared object type.
   *
   * @param relation relation to traverse
   * @param objectType caller-defined object type reached by the traversal
   * @param expression expression evaluated after traversal
   */
  public TraversalExpression(
      RelationRef relation, String objectType, PermissionExpression expression) {
    this(relation, Optional.of(validateObjectType(objectType)), expression);
  }

  /**
   * Creates a traversal expression.
   *
   * @param relation relation to traverse
   * @param objectType optional caller-defined object type reached by the traversal
   * @param expression expression evaluated after traversal
   */
  public TraversalExpression {
    relation = ExpressionValidator.value("relation", relation);
    objectType =
        objectType == null
            ? Optional.empty()
            : objectType.map(TraversalExpression::validateObjectType);
    expression = ExpressionValidator.expression("expression", expression);
  }

  private static String validateObjectType(String value) {
    return new com.luokuiai.forga.core.model.ObjectRef(value, "listing").type();
  }
}
