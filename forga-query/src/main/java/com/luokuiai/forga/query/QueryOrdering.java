package com.luokuiai.forga.query;

import java.util.Objects;

/**
 * Ordered field used by a generated authorization list query.
 *
 * @param field field to order by
 * @param direction sort direction
 */
public record QueryOrdering(QueryField field, QuerySortDirection direction) {

  /**
   * Creates an ordering.
   *
   * @param field field to order by
   * @param direction sort direction
   */
  public QueryOrdering {
    field = Objects.requireNonNull(field, "field is required");
    direction = Objects.requireNonNull(direction, "direction is required");
  }
}
