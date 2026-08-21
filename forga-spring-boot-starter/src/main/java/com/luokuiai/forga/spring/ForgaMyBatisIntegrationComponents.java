package com.luokuiai.forga.spring;

import com.luokuiai.forga.mybatis.ForgaMyBatisInterceptor;
import com.luokuiai.forga.mybatis.ForgaRequestAttributesProvider;
import com.luokuiai.forga.mybatis.ForgaSubjectProvider;
import com.luokuiai.forga.mybatis.MyBatisStatementRegistry;
import java.util.Objects;

/**
 * MyBatis integration components assembled by the starter.
 *
 * @param interceptor MyBatis interceptor
 * @param statements statement registry
 * @param subjects subject provider
 * @param attributes request attributes provider
 */
public record ForgaMyBatisIntegrationComponents(
    ForgaMyBatisInterceptor interceptor,
    MyBatisStatementRegistry statements,
    ForgaSubjectProvider subjects,
    ForgaRequestAttributesProvider attributes) {

  /**
   * Creates MyBatis integration components.
   *
   * @param interceptor MyBatis interceptor
   * @param statements statement registry
   * @param subjects subject provider
   * @param attributes request attributes provider
   */
  public ForgaMyBatisIntegrationComponents {
    interceptor = Objects.requireNonNull(interceptor, "interceptor is required");
    statements = Objects.requireNonNull(statements, "statements are required");
    subjects = Objects.requireNonNull(subjects, "subjects are required");
    attributes = Objects.requireNonNull(attributes, "attributes are required");
  }
}
