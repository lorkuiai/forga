package com.luokuiai.forga.mybatis;

import com.luokuiai.forga.query.QueryParameter;
import java.util.List;
import java.util.Objects;

/**
 * SQL predicate fragment and parameter references produced by safe translation.
 *
 * @param sql SQL predicate fragment
 * @param parameters bound parameter references in encounter order
 */
public record MyBatisBoundConstraint(String sql, List<QueryParameter> parameters) {

  /**
   * Creates a bound constraint.
   *
   * @param sql SQL predicate fragment
   * @param parameters bound parameter references in encounter order
   */
  public MyBatisBoundConstraint {
    if (sql == null || sql.isBlank()) {
      throw new IllegalArgumentException("sql is required");
    }
    sql = sql.trim();
    parameters = List.copyOf(Objects.requireNonNull(parameters, "parameters are required"));
  }
}
