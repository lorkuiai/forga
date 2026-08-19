package com.luokuiai.forga.query;

import java.util.List;
import java.util.Objects;

/**
 * Set-based ReBAC list query plan backed by an authorization rowset.
 *
 * @param join join between the business resource and authorization rowset
 * @param where authorization rowset predicates
 * @param projections authorization fields to select
 * @param orderings fields to order by before pagination
 */
public record AuthorizedListQuery(
    AuthorizedRowsetJoin join,
    QueryConstraint where,
    List<QueryProjection> projections,
    List<QueryOrdering> orderings) {

  /**
   * Creates an authorized list query plan.
   *
   * @param join rowset join
   * @param where authorization predicates
   * @param projections selected authorization fields
   * @param orderings ordering fields
   */
  public AuthorizedListQuery {
    join = Objects.requireNonNull(join, "join is required");
    where = Objects.requireNonNull(where, "where is required");
    projections = List.copyOf(Objects.requireNonNull(projections, "projections are required"));
    orderings = List.copyOf(Objects.requireNonNull(orderings, "orderings are required"));
  }
}
