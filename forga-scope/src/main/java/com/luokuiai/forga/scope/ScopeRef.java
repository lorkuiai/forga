package com.luokuiai.forga.scope;

import com.luokuiai.forga.core.model.ObjectRef;

/**
 * Opaque reference to an authorization boundary.
 *
 * @param type caller-defined scope type
 * @param id caller-defined stable scope id
 */
public record ScopeRef(String type, String id) {

  /**
   * Creates a scope reference.
   *
   * @param type caller-defined scope type
   * @param id caller-defined stable scope id
   */
  public ScopeRef {
    type = ScopeValidator.kind("scope type", type);
    id = ScopeValidator.value("scope id", id);
  }

  /**
   * Converts this scope to the object reference used by authorization checks.
   *
   * @return object reference for this scope
   */
  public ObjectRef toObjectRef() {
    return new ObjectRef(type, id);
  }
}
