package com.luokuiai.forga.query;

import java.util.Objects;

/**
 * Correlation between an outer field and an inner field.
 *
 * @param outer outer field
 * @param inner inner field
 */
public record QueryCorrelation(QueryField outer, QueryField inner) {

  /**
   * Creates a field correlation.
   *
   * @param outer outer field
   * @param inner inner field
   */
  public QueryCorrelation {
    outer = Objects.requireNonNull(outer, "outer is required");
    inner = Objects.requireNonNull(inner, "inner is required");
  }
}
