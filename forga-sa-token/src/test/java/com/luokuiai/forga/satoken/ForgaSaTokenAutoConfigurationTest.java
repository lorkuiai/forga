package com.luokuiai.forga.satoken;

import static org.assertj.core.api.Assertions.assertThat;

import cn.dev33.satoken.stp.StpLogic;
import com.luokuiai.forga.core.context.AuthenticatedSubjectProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class ForgaSaTokenAutoConfigurationTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(ForgaSaTokenAutoConfiguration.class));

  @Test
  void assemblesSaTokenProviderWhenStpLogicExists() {
    contextRunner
        .withPropertyValues("forga.authentication.subject-type=operator")
        .withBean(StpLogic.class, () -> new StubStpLogic("alice"))
        .run(
            context -> {
              assertThat(context).hasSingleBean(AuthenticatedSubjectProvider.class);
              assertThat(context.getBean(AuthenticatedSubjectProvider.class).currentSubject())
                  .hasValueSatisfying(
                      subject -> {
                        assertThat(subject.type()).isEqualTo("operator");
                        assertThat(subject.id()).isEqualTo("alice");
                      });
            });
  }

  @Test
  void failsWithoutStpLogic() {
    contextRunner.run(
        context -> {
          assertThat(context).hasFailed();
          assertThat(context.getStartupFailure())
              .hasRootCauseInstanceOf(NoSuchBeanDefinitionException.class);
        });
  }

  private static final class StubStpLogic extends StpLogic {

    private final String loginId;

    private StubStpLogic(String loginId) {
      super("test");
      this.loginId = loginId;
    }

    @Override
    public boolean isLogin() {
      return true;
    }

    @Override
    public String getLoginIdAsString() {
      return loginId;
    }
  }
}
