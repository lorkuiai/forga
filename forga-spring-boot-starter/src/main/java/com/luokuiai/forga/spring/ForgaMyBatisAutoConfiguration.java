package com.luokuiai.forga.spring;

import com.luokuiai.forga.mybatis.ForgaMyBatisInterceptor;
import com.luokuiai.forga.mybatis.ForgaRequestAttributesProvider;
import com.luokuiai.forga.mybatis.ForgaSubjectProvider;
import com.luokuiai.forga.mybatis.MyBatisResourceMapping;
import com.luokuiai.forga.mybatis.MyBatisStatementRegistry;
import com.luokuiai.forga.query.QueryResource;
import java.util.Map;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot auto-configuration for generic Forga MyBatis authorization.
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "forga", name = "enabled", havingValue = "true")
public class ForgaMyBatisAutoConfiguration {

  /**
   * Registers the Forga MyBatis interceptor when integration is enabled.
   *
   * @param properties integration properties
   * @param statements statement registry
   * @param subjects subject provider
   * @param attributes request attributes provider
   * @param mappings MyBatis resource mappings
   * @return MyBatis interceptor
   */
  @Bean
  @ConditionalOnMissingBean
  public ForgaMyBatisInterceptor forgaMyBatisInterceptor(
      ForgaIntegrationProperties properties,
      MyBatisStatementRegistry statements,
      ForgaSubjectProvider subjects,
      ForgaRequestAttributesProvider attributes,
      Map<QueryResource, MyBatisResourceMapping> mappings) {
    return ForgaMyBatisAutoConfigurationSupport.assemble(
            properties, statements, subjects, attributes, mappings)
        .orElseThrow(() -> new ForgaRuntimeException("Forga MyBatis integration is disabled"))
        .interceptor();
  }
}
