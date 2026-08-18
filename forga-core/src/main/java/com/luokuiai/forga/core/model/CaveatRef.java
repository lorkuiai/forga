package com.luokuiai.forga.core.model;

/**
 * Opaque caveat name selected by a policy.
 *
 * @param name caller-defined caveat name
 */
public record CaveatRef(String name) {

  /**
   * Creates a caveat reference.
   *
   * @param name caller-defined caveat name
   */
  public CaveatRef {
    name = ReferenceValidator.kind("caveat name", name);
  }
}
