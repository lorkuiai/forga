package com.luokuiai.forga.scope;

import com.luokuiai.forga.query.QueryConstraint;
import com.luokuiai.forga.query.QueryParameter;
import com.luokuiai.forga.query.QueryValueType;
import com.luokuiai.forga.query.PredicateOperator;
import com.luokuiai.forga.query.ResourceQueryMapping;
import java.util.List;
import java.util.Map;

/**
 * Helpers for creating active-scope query constraints.
 */
public final class ScopeQueryConstraints {

  /** Default parameter name for the active scope type. */
  public static final String SCOPE_TYPE_PARAMETER = "forga_scope_type";

  /** Default parameter name for the active scope id. */
  public static final String SCOPE_ID_PARAMETER = "forga_scope_id";

  private ScopeQueryConstraints() {
  }

  /**
   * Creates a two-field predicate binding a resource to the active scope.
   *
   * @param mapping allowlisted resource mapping
   * @param scopeTypeField field storing the scope type
   * @param scopeIdField field storing the scope id
   * @return AND constraint for scope type and id
   */
  public static QueryConstraint activeScope(
      ResourceQueryMapping mapping, String scopeTypeField, String scopeIdField) {
    return QueryConstraint.and(
        List.of(
            QueryConstraint.predicate(
                mapping.field(scopeTypeField),
                PredicateOperator.EQUALS,
                new QueryParameter(SCOPE_TYPE_PARAMETER, QueryValueType.STRING)),
            QueryConstraint.predicate(
                mapping.field(scopeIdField),
                PredicateOperator.EQUALS,
                new QueryParameter(SCOPE_ID_PARAMETER, QueryValueType.STRING))));
  }

  /**
   * Creates a constraint allowing objects in the active scope or matched by an explicit grant.
   *
   * <p>The grant constraint must be set-based and supplied by the host application. The result is
   * only an ownership-or-grant boundary fragment; hosts must combine it with ordinary object
   * permission constraints and verify scope entry before executing the query. This helper does not
   * perform per-object authorization checks or prescribe grant storage.
   *
   * @param mapping allowlisted resource mapping
   * @param scopeTypeField field storing the scope type
   * @param scopeIdField field storing the scope id
   * @param granted host-provided explicit grant constraint
   * @return OR constraint for active-scope ownership or an explicit grant
   */
  public static QueryConstraint activeOrGranted(
      ResourceQueryMapping mapping,
      String scopeTypeField,
      String scopeIdField,
      QueryConstraint granted) {
    if (granted == null) {
      throw new IllegalArgumentException("granted constraint is required");
    }
    return QueryConstraint.or(
        List.of(activeScope(mapping, scopeTypeField, scopeIdField), granted));
  }

  /**
   * Creates the parameter values required by {@link #activeScope}.
   *
   * @param activeScope active scope
   * @return parameter map
   */
  public static Map<String, String> parameters(ActiveScope activeScope) {
    if (activeScope == null) {
      throw new IllegalArgumentException("active scope is required");
    }
    return Map.of(
        SCOPE_TYPE_PARAMETER, activeScope.scope().type(),
        SCOPE_ID_PARAMETER, activeScope.scope().id());
  }
}
