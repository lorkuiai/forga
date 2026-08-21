package com.luokuiai.forga.mybatis;

import com.luokuiai.forga.query.QueryParameter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * SQL text and bound authorization parameters after optional constraint application.
 *
 * @param sql SQL text
 * @param parameters authorization parameter references
 * @param parameterValues authorization parameter values keyed by parameter name
 */
public record MyBatisBoundSql(
    String sql, List<QueryParameter> parameters, Map<String, Object> parameterValues) {

  /**
   * Creates bound SQL without parameter values.
   *
   * @param sql SQL text
   * @param parameters authorization parameter references
   */
  public MyBatisBoundSql(String sql, List<QueryParameter> parameters) {
    this(sql, parameters, Map.of());
  }

  /**
   * Creates bound SQL.
   *
   * @param sql SQL text
   * @param parameters authorization parameter references
   * @param parameterValues authorization parameter values
   */
  public MyBatisBoundSql {
    if (sql == null || sql.isBlank()) {
      throw new IllegalArgumentException("sql is required");
    }
    sql = sql.trim();
    parameters = List.copyOf(Objects.requireNonNull(parameters, "parameters are required"));
    parameterValues =
        Map.copyOf(Objects.requireNonNull(parameterValues, "parameterValues are required"));
  }
}
