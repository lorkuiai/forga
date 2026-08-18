package com.luokuiai.forga.query;

import java.util.Objects;

/**
 * Typed field reference owned by a caller-defined resource.
 *
 * @param resource resource containing the field
 * @param name field name
 */
public record QueryField(QueryResource resource, String name) {

  /**
   * Creates a query field.
   *
   * @param resource resource containing the field
   * @param name field name
   */
  public QueryField {
    resource = Objects.requireNonNull(resource, "resource is required");
    name = QueryValidator.name("field name", name);
  }
}
