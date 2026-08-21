package com.luokuiai.forga.scope;

/**
 * Resolves explicit access from an active scope to an object owned by another scope.
 */
@FunctionalInterface
public interface CrossScopeAccessResolver {

  /**
   * Returns whether the exact cross-scope request is explicitly allowed.
   *
   * <p>Implementations should observe the same request-consistent host data snapshot as object
   * scope and relationship resolution. Collection queries should use an equivalent set-based
   * constraint
   * instead of invoking this resolver once per row.
   *
   * @param request cross-scope access request
   * @return true only when the host can prove an explicit grant
   */
  boolean allows(CrossScopeAccessRequest request);

  /**
   * Returns a resolver that denies every cross-scope request.
   *
   * @return deny-by-default resolver
   */
  static CrossScopeAccessResolver denyAll() {
    return request -> false;
  }
}
