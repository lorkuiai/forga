package com.luokuiai.forga.mybatis;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Applies at most one composed authorization constraint at a declared MyBatis boundary.
 */
public final class MyBatisConstraintApplicator {

  private final MyBatisConstraintTranslator translator;

  /**
   * Creates a constraint applicator.
   *
   * @param translator typed constraint translator
   */
  public MyBatisConstraintApplicator(MyBatisConstraintTranslator translator) {
    this.translator = Objects.requireNonNull(translator, "translator is required");
  }

  /**
   * Applies a boundary constraint when enabled.
   *
   * @param sql original SQL
   * @param boundary optional authorization boundary
   * @param enabled whether authorization is enabled
   * @return SQL and authorization parameter references
   */
  public MyBatisBoundSql apply(
      String sql, Optional<MyBatisAuthorizationBoundary> boundary, boolean enabled) {
    if (sql == null || sql.isBlank()) {
      throw new IllegalArgumentException("sql is required");
    }
    String original = sql.trim();
    if (!enabled || boundary.isEmpty()) {
      return new MyBatisBoundSql(original, List.of());
    }
    MyBatisBoundConstraint constraint = translator.translate(boundary.orElseThrow().constraint());
    String separator = original.toLowerCase(Locale.ROOT).contains(" where ") ? " AND " : " WHERE ";
    return new MyBatisBoundSql(
        original + separator + "(" + constraint.sql() + ")", constraint.parameters());
  }
}
