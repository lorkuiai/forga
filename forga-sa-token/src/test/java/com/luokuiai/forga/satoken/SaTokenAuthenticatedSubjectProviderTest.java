package com.luokuiai.forga.satoken;

import static org.assertj.core.api.Assertions.assertThat;

import cn.dev33.satoken.stp.StpLogic;
import com.luokuiai.forga.core.model.SubjectRef;
import org.junit.jupiter.api.Test;

class SaTokenAuthenticatedSubjectProviderTest {

  @Test
  void mapsAuthenticatedLoginId() {
    SaTokenAuthenticatedSubjectProvider provider =
        new SaTokenAuthenticatedSubjectProvider(new StubStpLogic(true, "alice"));

    assertThat(provider.currentSubject()).contains(new SubjectRef("user", "alice"));
  }

  @Test
  void returnsEmptyWithoutLogin() {
    SaTokenAuthenticatedSubjectProvider provider =
        new SaTokenAuthenticatedSubjectProvider(new StubStpLogic(false, null));

    assertThat(provider.currentSubject()).isEmpty();
  }

  private static final class StubStpLogic extends StpLogic {

    private final boolean loggedIn;

    private final String loginId;

    private StubStpLogic(boolean loggedIn, String loginId) {
      super("test");
      this.loggedIn = loggedIn;
      this.loginId = loginId;
    }

    @Override
    public boolean isLogin() {
      return loggedIn;
    }

    @Override
    public String getLoginIdAsString() {
      return loginId;
    }
  }
}
