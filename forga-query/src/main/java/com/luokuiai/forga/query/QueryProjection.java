package com.luokuiai.forga.query;

import java.util.Objects;

/**
 * Field projected by a generated authorization list query.
 *
 * @param field field to select
 * @param alias stable SQL alias
 */
public record QueryProjection(QueryField field, String alias) {

  /**
   * Creates a projection.
   *
   * @param field field to select
   * @param alias stable SQL alias
   */
  public QueryProjection {
    field = Objects.requireNonNull(field, "field is required");
    alias = QueryValidator.name("alias", alias);
  }
}
