package com.luokuiai.forga.core.model;

/**
 * Opaque reference to a protected object.
 *
 * @param type caller-defined object type
 * @param id caller-defined stable object id
 */
public record ObjectRef(String type, String id) {

  /**
   * Creates an object reference.
   *
   * @param type caller-defined object type
   * @param id caller-defined stable object id
   */
  public ObjectRef {
    type = ReferenceValidator.kind("object type", type);
    id = ReferenceValidator.value("object id", id);
  }
}
