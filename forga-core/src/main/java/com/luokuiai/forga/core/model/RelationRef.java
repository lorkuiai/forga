package com.luokuiai.forga.core.model;

/**
 * Opaque relation name used by policy expressions and resolvers.
 *
 * @param name caller-defined relation name
 */
public record RelationRef(String name) {

  /**
   * Creates a relation reference.
   *
   * @param name caller-defined relation name
   */
  public RelationRef {
    name = ReferenceValidator.kind("relation name", name);
  }
}
