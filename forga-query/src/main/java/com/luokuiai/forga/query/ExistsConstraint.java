package com.luokuiai.forga.query;

import java.util.List;
import java.util.Objects;

/**
 * Correlated existence constraint over a related resource.
 *
 * @param resource resource whose existence is required
 * @param joins typed joins needed by the existence branch
 * @param where predicate applied inside the existence branch
 */
public record ExistsConstraint(
    QueryResource resource, List<QueryJoin> joins, QueryConstraint where)
    implements QueryConstraint {

  /**
   * Creates an existence constraint.
   *
   * @param resource resource whose existence is required
   * @param joins typed joins needed by the existence branch
   * @param where predicate applied inside the existence branch
   */
  public ExistsConstraint {
    resource = Objects.requireNonNull(resource, "resource is required");
    joins = List.copyOf(joins);
    where = Objects.requireNonNull(where, "where is required");
  }
}
