package com.luokuiai.forga.core.model;

/**
 * Opaque attribute name requested from an attribute resolver.
 *
 * @param name caller-defined attribute name
 */
public record AttributeRef(String name) {

  /**
   * Creates an attribute reference.
   *
   * @param name caller-defined attribute name
   */
  public AttributeRef {
    name = ReferenceValidator.kind("attribute name", name);
  }
}
