package com.luokuiai.forga.spring;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class ForgaBannerAutoConfigurationTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(ForgaBannerAutoConfiguration.class));

  @Test
  void registersVersionedBannerWhenForgaIsEnabled() {
    contextRunner
        .withPropertyValues("forga.enabled=true")
        .run(
            context -> {
              assertThat(context).hasBean("forgaStartupBanner");
              assertThat(ForgaStartupBanner.render())
                  .contains("Forga")
                  .contains("Fine-grained Object-Relation Graph Authorization")
                  .contains("(v" + ForgaStartupBanner.version() + ")");
              assertThat(ForgaStartupBanner.version()).isNotBlank().doesNotStartWith("${");
            });
  }

  @Test
  void registersNoBannerWhenForgaIsDisabled() {
    contextRunner
        .withPropertyValues("forga.enabled=false")
        .run(context -> assertThat(context).doesNotHaveBean("forgaStartupBanner"));
  }
}
