package com.luokuiai.forga.query;

import java.util.List;
import java.util.Objects;

/**
 * Typed join between two caller-defined resources.
 *
 * @param source source resource
 * @param target target resource
 * @param correlations correlated field pairs
 */
public record QueryJoin(
    QueryResource source, QueryResource target, List<QueryCorrelation> correlations) {

  /**
   * Creates a query join.
   *
   * @param source source resource
   * @param target target resource
   * @param correlations correlated field pairs
   */
  public QueryJoin {
    source = Objects.requireNonNull(source, "source is required");
    target = Objects.requireNonNull(target, "target is required");
    correlations = List.copyOf(correlations);
    if (correlations.isEmpty()) {
      throw new IllegalArgumentException("join requires at least one correlation");
    }
  }
}
