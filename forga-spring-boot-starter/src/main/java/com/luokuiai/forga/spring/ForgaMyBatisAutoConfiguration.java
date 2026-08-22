package com.luokuiai.forga.spring;

import com.luokuiai.forga.core.context.AuthenticatedSubjectProvider;
import com.luokuiai.forga.core.context.AuthorizationAttributesProvider;
import com.luokuiai.forga.mybatis.ForgaMyBatisInterceptor;
import com.luokuiai.forga.mybatis.MyBatisResourceMapping;
import com.luokuiai.forga.mybatis.MyBatisStatementRegistry;
import com.luokuiai.forga.query.QueryResource;
import java.util.Map;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot auto-configuration for generic Forga MyBatis authorization.
 */
@AutoConfiguration
@ConditionalOnForgaEnabled
public class ForgaMyBatisAutoConfiguration {

  /**
   * Registers the Forga MyBatis interceptor when integration is enabled.
   *
   * @param statements statement registry
   * @param subjects subject provider
   * @param attributes request attributes provider
   * @param mappings MyBatis resource mappings
   * @return MyBatis interceptor
   */
  @Bean
  @ConditionalOnMissingBean
  public ForgaMyBatisInterceptor forgaMyBatisInterceptor(
      MyBatisStatementRegistry statements,
      AuthenticatedSubjectProvider subjects,
      AuthorizationAttributesProvider attributes,
      Map<QueryResource, MyBatisResourceMapping> mappings) {
    return ForgaMyBatisAutoConfigurationSupport.assemble(
            statements, subjects, attributes, mappings)
        .interceptor();
  }
}
