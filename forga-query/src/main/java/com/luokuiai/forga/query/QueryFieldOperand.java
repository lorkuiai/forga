package com.luokuiai.forga.query;

import java.util.Objects;

/**
 * Field operand used inside typed predicates.
 *
 * @param field field reference
 */
public record QueryFieldOperand(QueryField field) implements QueryOperand {

  /**
   * Creates a field operand.
   *
   * @param field field reference
   */
  public QueryFieldOperand {
    field = Objects.requireNonNull(field, "field is required");
  }
}
