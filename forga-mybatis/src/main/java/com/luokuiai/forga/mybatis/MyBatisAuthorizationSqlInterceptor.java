package com.luokuiai.forga.mybatis;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Framework-neutral SQL interception logic used by the MyBatis plugin.
 */
public final class MyBatisAuthorizationSqlInterceptor {

  private final MyBatisStatementRegistry statements;

  private final ForgaSubjectProvider subjects;

  private final ForgaRequestAttributesProvider attributes;

  private final MyBatisConstraintApplicator applicator;

  private final boolean enabled;

  /**
   * Creates SQL interception support.
   *
   * @param statements statement registry
   * @param subjects subject provider
   * @param attributes request attributes provider
   * @param applicator constraint applicator
   * @param enabled whether authorization is enabled
   */
  public MyBatisAuthorizationSqlInterceptor(
      MyBatisStatementRegistry statements,
      ForgaSubjectProvider subjects,
      ForgaRequestAttributesProvider attributes,
      MyBatisConstraintApplicator applicator,
      boolean enabled) {
    this.statements = Objects.requireNonNull(statements, "statements are required");
    this.subjects = Objects.requireNonNull(subjects, "subjects are required");
    this.attributes = Objects.requireNonNull(attributes, "attributes are required");
    this.applicator = Objects.requireNonNull(applicator, "applicator is required");
    this.enabled = enabled;
  }

  /**
   * Applies authorization to SQL for one statement id.
   *
   * @param statementId MyBatis mapped statement id
   * @param sql original SQL
   * @return authorized SQL and bound authorization parameters
   */
  public MyBatisBoundSql intercept(String statementId, String sql) {
    MyBatisStatementAuthorization statement = statements.find(statementId).orElse(null);
    if (!enabled || statement == null) {
      return applicator.apply(sql, java.util.Optional.empty(), false);
    }
    if (!isSelect(sql)) {
      throw new MyBatisAuthorizationException("only SELECT statements can be authorized");
    }
    subjects.currentSubject()
        .orElseThrow(() -> new MyBatisAuthorizationException("authorization subject is missing"));
    Map.copyOf(attributes.attributes());
    return applicator.apply(sql, java.util.Optional.of(statement.boundary()), true);
  }

  private static boolean isSelect(String sql) {
    return sql != null && sql.stripLeading().toLowerCase(Locale.ROOT).startsWith("select ");
  }
}
