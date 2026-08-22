package com.luokuiai.forga.spring;

import static org.assertj.core.api.Assertions.assertThat;

import com.luokuiai.forga.core.context.AuthenticatedSubjectProvider;
import com.luokuiai.forga.core.context.AuthorizationAttributesProvider;
import com.luokuiai.forga.mybatis.ForgaMyBatisInterceptor;
import com.luokuiai.forga.mybatis.MyBatisResourceMapping;
import com.luokuiai.forga.mybatis.MyBatisStatementRegistry;
import com.luokuiai.forga.query.QueryResource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class ForgaEnablementTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(ForgaMyBatisAutoConfiguration.class));

  @Test
  void annotationEnablesMyBatisIntegration() {
    contextRunner
        .withUserConfiguration(EnabledConfiguration.class)
        .run(context -> assertThat(context).hasSingleBean(ForgaMyBatisInterceptor.class));
  }

  @Test
  void legacyPropertyCannotEnableMyBatisIntegration() {
    contextRunner
        .withPropertyValues("forga.enabled=true")
        .run(
            context -> {
              assertThat(context).hasNotFailed();
              assertThat(context).doesNotHaveBean(ForgaMyBatisInterceptor.class);
            });
  }

  @Configuration(proxyBeanMethods = false)
  @EnableForga
  static class EnabledConfiguration {

    @Bean
    MyBatisStatementRegistry myBatisStatementRegistry() {
      return new MyBatisStatementRegistry(List.of());
    }

    @Bean
    AuthenticatedSubjectProvider authenticatedSubjectProvider() {
      return Optional::empty;
    }

    @Bean
    AuthorizationAttributesProvider authorizationAttributesProvider() {
      return Map::of;
    }

    @Bean
    Map<QueryResource, MyBatisResourceMapping> myBatisResourceMappings() {
      return Map.of();
    }
  }
}
