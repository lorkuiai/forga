package com.luokuiai.forga.mybatis;

import com.luokuiai.forga.query.QueryConstraint;
import java.util.Objects;

/**
 * Declared integration boundary for applying one composed authorization constraint.
 *
 * @param id boundary id
 * @param constraint composed authorization constraint
 */
public record MyBatisAuthorizationBoundary(String id, QueryConstraint constraint) {

  /**
   * Creates an authorization boundary.
   *
   * @param id boundary id
   * @param constraint composed authorization constraint
   */
  public MyBatisAuthorizationBoundary {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("id is required");
    }
    id = id.trim();
    constraint = Objects.requireNonNull(constraint, "constraint is required");
  }
}
