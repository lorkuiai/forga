package com.luokuiai.forga.spring;

import static org.assertj.core.api.Assertions.assertThat;

import cn.dev33.satoken.stp.StpLogic;
import com.luokuiai.forga.core.context.AuthenticatedSubjectProvider;
import com.luokuiai.forga.satoken.ForgaSaTokenAutoConfiguration;
import com.luokuiai.forga.spring.security.ForgaSpringSecurityAutoConfiguration;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

class ForgaAuthenticationProviderAutoConfigurationTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(
              AutoConfigurations.of(ForgaAuthenticationProviderAutoConfiguration.class))
          .withUserConfiguration(EnabledConfiguration.class);

  @Test
  void startsWithExactlyOneAuthenticationProvider() {
    contextRunner
        .withBean(
            AuthenticatedSubjectProvider.class,
            () -> () -> Optional.empty())
        .run(
            context -> {
              assertThat(context).hasNotFailed();
              assertThat(context).hasSingleBean(AuthenticatedSubjectProvider.class);
            });
  }

  @Test
  void failsWithoutAuthenticationProvider() {
    contextRunner.run(
        context -> {
          assertThat(context).hasFailed();
          assertThat(context.getStartupFailure())
              .hasMessage("enabled integration requires an authentication provider");
        });
  }

  @Test
  void failsWithMultipleAuthenticationProviders() {
    contextRunner
        .withBean(
            "firstProvider",
            AuthenticatedSubjectProvider.class,
            () -> () -> Optional.empty())
        .withBean(
            "secondProvider",
            AuthenticatedSubjectProvider.class,
            () -> () -> Optional.empty())
        .run(
            context -> {
              assertThat(context).hasFailed();
              assertThat(context.getStartupFailure())
                  .hasMessage(
                      "enabled integration requires exactly one authentication provider, found 2");
            });
  }

  @Test
  void failsWhenSaTokenAndSpringSecurityAdaptersAreBothActive() {
    new ApplicationContextRunner()
        .withConfiguration(
            AutoConfigurations.of(
                ForgaSaTokenAutoConfiguration.class,
                ForgaSpringSecurityAutoConfiguration.class,
                ForgaAuthenticationProviderAutoConfiguration.class))
        .withUserConfiguration(EnabledConfiguration.class)
        .withBean(StpLogic.class, () -> new StpLogic("test"))
        .run(
            context -> {
              assertThat(context).hasFailed();
              assertThat(context.getStartupFailure())
                  .hasMessage(
                      "enabled integration requires exactly one authentication provider, found 2");
            });
  }

  @Test
  void legacyPropertyDoesNotRequireAuthenticationProvider() {
    new ApplicationContextRunner()
        .withPropertyValues("forga.enabled=true")
        .withConfiguration(
            AutoConfigurations.of(ForgaAuthenticationProviderAutoConfiguration.class))
        .run(
            context -> {
              assertThat(context).hasNotFailed();
              assertThat(context).doesNotHaveBean("forgaAuthenticationProviderValidation");
            });
  }

  @Configuration(proxyBeanMethods = false)
  @EnableForga
  static class EnabledConfiguration { }
}
