package com.luokuiai.forga.mybatis;

import com.luokuiai.forga.query.AuthorizedListQuery;
import com.luokuiai.forga.query.QueryConstraint;
import java.util.Objects;
import java.util.Optional;

/**
 * Declared integration boundary for applying one composed authorization constraint.
 *
 * @param id boundary id
 * @param constraint composed authorization constraint
 * @param listQuery set-based authorized list query
 */
public record MyBatisAuthorizationBoundary(
    String id, QueryConstraint constraint, AuthorizedListQuery listQuery) {

  /**
   * Creates a predicate boundary.
   *
   * @param id boundary id
   * @param constraint composed authorization constraint
   */
  public MyBatisAuthorizationBoundary(String id, QueryConstraint constraint) {
    this(id, constraint, null);
  }

  /**
   * Creates an authorization boundary.
   *
   * @param id boundary id
   * @param constraint composed authorization constraint
   * @param listQuery set-based authorized list query
   */
  public MyBatisAuthorizationBoundary {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("id is required");
    }
    id = id.trim();
    if ((constraint == null) == (listQuery == null)) {
      throw new IllegalArgumentException("exactly one boundary type is required");
    }
  }

  /**
   * Creates an authorized list boundary.
   *
   * @param id boundary id
   * @param listQuery set-based authorized list query
   * @return authorization boundary
   */
  public static MyBatisAuthorizationBoundary list(String id, AuthorizedListQuery listQuery) {
    return new MyBatisAuthorizationBoundary(
        id, null, Objects.requireNonNull(listQuery, "listQuery is required"));
  }

  /**
   * Returns the predicate boundary when present.
   *
   * @return predicate boundary
   */
  public Optional<QueryConstraint> predicate() {
    return Optional.ofNullable(constraint);
  }

  /**
   * Returns the authorized list query when present.
   *
   * @return authorized list query
   */
  public Optional<AuthorizedListQuery> authorizedList() {
    return Optional.ofNullable(listQuery);
  }
}
