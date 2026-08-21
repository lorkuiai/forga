package com.luokuiai.forga.mybatis;

import com.luokuiai.forga.query.QueryParameter;
import java.util.List;
import java.util.Objects;

/**
 * SQL text and bound authorization parameters after optional constraint application.
 *
 * @param sql SQL text
 * @param parameters authorization parameter references
 */
public record MyBatisBoundSql(String sql, List<QueryParameter> parameters) {

  /**
   * Creates bound SQL.
   *
   * @param sql SQL text
   * @param parameters authorization parameter references
   */
  public MyBatisBoundSql {
    if (sql == null || sql.isBlank()) {
      throw new IllegalArgumentException("sql is required");
    }
    sql = sql.trim();
    parameters = List.copyOf(Objects.requireNonNull(parameters, "parameters are required"));
  }
}
