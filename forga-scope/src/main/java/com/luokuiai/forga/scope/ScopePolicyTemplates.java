package com.luokuiai.forga.scope;

import com.luokuiai.forga.core.model.PermissionRef;
import com.luokuiai.forga.core.model.RelationRef;
import com.luokuiai.forga.core.policy.PermissionExpression;
import com.luokuiai.forga.core.policy.PolicyDefinition;
import java.util.List;
import java.util.Map;

/**
 * Builders for common scope authorization policy expressions.
 */
public final class ScopePolicyTemplates {

  /** Default relation for direct membership in a scope. */
  public static final RelationRef MEMBER = new RelationRef("member");

  /** Default relation for scoped role assignment. */
  public static final RelationRef ASSIGNED = new RelationRef("assigned");

  /** Default relation that removes access when matched. */
  public static final RelationRef DENIED = new RelationRef("denied");

  /** Default permission for entering a scope. */
  public static final PermissionRef ENTER = new PermissionRef("enter");

  private ScopePolicyTemplates() {
  }

  /**
   * Builds an expression that grants access through direct scope membership.
   *
   * @return membership expression
   */
  public static PermissionExpression membership() {
    return PermissionExpression.relation(MEMBER);
  }

  /**
   * Builds an expression that grants access through scoped assignment.
   *
   * @return assignment expression
   */
  public static PermissionExpression assignment() {
    return PermissionExpression.relation(ASSIGNED);
  }

  /**
   * Builds a policy expression allowing membership or assignment unless denied.
   *
   * @return default scope entry expression
   */
  public static PermissionExpression enterScope() {
    return PermissionExpression.exclusion(
        PermissionExpression.union(List.of(membership(), assignment())),
        PermissionExpression.relation(DENIED));
  }

  /**
   * Builds a minimal policy definition for the default scope entry permission.
   *
   * @return policy definition keyed by {@link #ENTER}
   */
  public static PolicyDefinition enterScopePolicy() {
    return new PolicyDefinition(Map.of(ENTER, enterScope()));
  }
}
