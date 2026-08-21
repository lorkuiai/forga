package com.luokuiai.forga.spring.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.luokuiai.forga.core.model.SubjectRef;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class SpringSecurityAuthenticatedSubjectProviderTest {

  @Test
  void mapsAuthenticatedPrincipalWithoutUsingAuthorities() {
    TestingAuthenticationToken authentication =
        new TestingAuthenticationToken("alice", "ignored", "business:permission");
    SpringSecurityAuthenticatedSubjectProvider provider =
        new SpringSecurityAuthenticatedSubjectProvider(() -> authentication, "account");

    assertThat(provider.currentSubject()).contains(new SubjectRef("account", "alice"));
  }

  @Test
  void returnsEmptyForMissingAndUnauthenticatedContext() {
    TestingAuthenticationToken unauthenticated =
        new TestingAuthenticationToken("alice", "ignored");
    unauthenticated.setAuthenticated(false);

    SpringSecurityAuthenticatedSubjectProvider missing =
        new SpringSecurityAuthenticatedSubjectProvider(() -> null, "account");

    assertThat(missing.currentSubject())
        .isEmpty();
    assertThat(
            new SpringSecurityAuthenticatedSubjectProvider(
                    () -> unauthenticated, "account")
                .currentSubject())
        .isEmpty();
  }

  @Test
  void returnsEmptyForAnonymousAuthentication() {
    AnonymousAuthenticationToken anonymous =
        new AnonymousAuthenticationToken(
            "key", "anonymous", List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));

    SpringSecurityAuthenticatedSubjectProvider provider =
        new SpringSecurityAuthenticatedSubjectProvider(() -> anonymous, "account");

    assertThat(provider.currentSubject()).isEmpty();
  }
}
