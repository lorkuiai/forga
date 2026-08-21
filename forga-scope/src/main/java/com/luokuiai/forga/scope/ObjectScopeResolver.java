package com.luokuiai.forga.scope;

import com.luokuiai.forga.core.model.ObjectRef;
import java.util.Optional;

/**
 * Resolves the host-owned authorization scope containing an object.
 */
@FunctionalInterface
public interface ObjectScopeResolver {

  /**
   * Resolves the owning scope for an object.
   *
   * <p>An empty result means the object cannot be proven to belong to a scope and causes strict
   * scoped authorization to fail closed. Implementations should observe the same request-consistent
   * host data snapshot as the relationship and cross-scope resolvers used by the authorization
   * service.
   *
   * @param object object whose owning scope is required
   * @return owning scope when the host can resolve it
   */
  Optional<ScopeRef> resolve(ObjectRef object);
}
