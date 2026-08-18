package com.luokuiai.forga.core.model;

/**
 * Opaque consistency marker propagated across resolver calls.
 *
 * @param value caller-defined consistency marker
 */
public record ConsistencyToken(String value) {

  /**
   * Creates a consistency token.
   *
   * @param value caller-defined consistency marker
   */
  public ConsistencyToken {
    value = ReferenceValidator.value("consistency token", value);
  }
}
