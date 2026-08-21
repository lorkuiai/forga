package com.luokuiai.forga.resolver;

import com.luokuiai.forga.core.model.AttributeRef;
import java.util.Objects;

/**
 * Opaque attribute value returned by a resolver.
 *
 * @param attribute attribute reference
 * @param value opaque attribute value
 */
public record ResolvedAttribute(AttributeRef attribute, String value) {

  /**
   * Creates a resolved attribute.
   *
   * @param attribute attribute reference
   * @param value opaque attribute value
   */
  public ResolvedAttribute {
    attribute = Objects.requireNonNull(attribute, "attribute is required");
    value = Objects.requireNonNull(value, "value is required");
  }
}
