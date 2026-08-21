package com.luokuiai.forga.spring.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.luokuiai.forga.core.context.AuthenticatedSubjectProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class ForgaSpringSecurityAutoConfigurationTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(ForgaSpringSecurityAutoConfiguration.class));

  @Test
  void assemblesSpringSecurityProvider() {
    contextRunner.run(
        context -> assertThat(context).hasSingleBean(AuthenticatedSubjectProvider.class));
  }
}
