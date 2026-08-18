package com.luokuiai.forga.spring;

import com.luokuiai.forga.mybatis.ForgaMyBatisInterceptor;
import com.luokuiai.forga.mybatis.ForgaRequestAttributesProvider;
import com.luokuiai.forga.mybatis.ForgaSubjectProvider;
import com.luokuiai.forga.mybatis.MyBatisAuthorizationSqlInterceptor;
import com.luokuiai.forga.mybatis.MyBatisConstraintApplicator;
import com.luokuiai.forga.mybatis.MyBatisConstraintTranslator;
import com.luokuiai.forga.mybatis.MyBatisResourceMapping;
import com.luokuiai.forga.mybatis.MyBatisStatementRegistry;
import com.luokuiai.forga.query.QueryResource;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Framework-neutral factory used by Spring auto-configuration.
 */
public final class ForgaMyBatisAutoConfigurationSupport {

  private ForgaMyBatisAutoConfigurationSupport() {
  }

  /**
   * Builds MyBatis integration components when enabled.
   *
   * @param properties integration properties
   * @param statements statement registry
   * @param subjects subject provider
   * @param attributes request attributes provider
   * @param mappings MyBatis resource mappings
   * @return components when enabled
   */
  public static Optional<ForgaMyBatisIntegrationComponents> assemble(
      ForgaIntegrationProperties properties,
      MyBatisStatementRegistry statements,
      ForgaSubjectProvider subjects,
      ForgaRequestAttributesProvider attributes,
      Map<QueryResource, MyBatisResourceMapping> mappings) {
    Objects.requireNonNull(properties, "properties are required");
    if (!properties.enabled()) {
      return Optional.empty();
    }
    Objects.requireNonNull(statements, "statements are required");
    Objects.requireNonNull(subjects, "subjects are required");
    Objects.requireNonNull(attributes, "attributes are required");
    MyBatisConstraintTranslator translator = new MyBatisConstraintTranslator(mappings);
    MyBatisAuthorizationSqlInterceptor sqlInterceptor =
        new MyBatisAuthorizationSqlInterceptor(
            statements,
            subjects,
            attributes,
            new MyBatisConstraintApplicator(translator),
            true);
    return Optional.of(
        new ForgaMyBatisIntegrationComponents(
            new ForgaMyBatisInterceptor(sqlInterceptor), statements, subjects, attributes));
  }
}
