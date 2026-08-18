package com.luokuiai.forga.query;

import java.util.Objects;

/**
 * Bound query parameter reference.
 *
 * @param name parameter name
 * @param type parameter value type
 */
public record QueryParameter(String name, QueryValueType type) implements QueryOperand {

  /**
   * Creates a query parameter.
   *
   * @param name parameter name
   * @param type parameter value type
   */
  public QueryParameter {
    name = QueryValidator.name("parameter name", name);
    type = Objects.requireNonNull(type, "type is required");
  }
}
