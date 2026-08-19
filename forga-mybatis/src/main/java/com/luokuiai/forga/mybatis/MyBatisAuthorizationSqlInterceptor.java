package com.luokuiai.forga.mybatis;

import com.luokuiai.forga.core.model.SubjectRef;
import com.luokuiai.forga.query.QueryParameter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
    SubjectRef subject =
        subjects.currentSubject()
            .orElseThrow(
                () -> new MyBatisAuthorizationException("authorization subject is missing"));
    Map<String, String> requestAttributes =
        attributes.attributes().entrySet().stream()
            .collect(
                Collectors.toUnmodifiableMap(
                    entry -> entry.getKey().name(), Map.Entry::getValue));
    MyBatisBoundSql bound =
        applicator.apply(sql, java.util.Optional.of(statement.boundary()), true);
    Map<String, Object> parameterValues = parameterValues(subject, requestAttributes, bound);
    return new MyBatisBoundSql(bound.sql(), bound.parameters(), parameterValues);
  }

  private static Map<String, Object> parameterValues(
      SubjectRef subject,
      Map<String, String> attributes,
      MyBatisBoundSql bound) {
    Map<String, Object> values = new HashMap<>();
    values.put("subject", subject.id());
    values.put("subject_id", subject.id());
    values.put("subject_type", subject.type());
    values.putAll(attributes);
    for (QueryParameter parameter : bound.parameters()) {
      if (!values.containsKey(parameter.name())) {
        throw new MyBatisAuthorizationException(
            "authorization parameter is missing: " + parameter.name());
      }
    }
    return values;
  }

  private static boolean isSelect(String sql) {
    return sql != null && sql.stripLeading().toLowerCase(Locale.ROOT).startsWith("select ");
  }
}
