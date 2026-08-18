package com.luokuiai.forga.core.model;

/**
 * Opaque permission name selected by a policy.
 *
 * @param name caller-defined permission name
 */
public record PermissionRef(String name) {

  /**
   * Creates a permission reference.
   *
   * @param name caller-defined permission name
   */
  public PermissionRef {
    name = ReferenceValidator.kind("permission name", name);
  }
}
