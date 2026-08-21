package com.luokuiai.forga.spring.security;

import com.luokuiai.forga.core.context.AuthenticatedSubjectProvider;
import com.luokuiai.forga.core.model.SubjectRef;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/** Maps Spring Security authentication state to a Forga authorization subject. */
public final class SpringSecurityAuthenticatedSubjectProvider
    implements AuthenticatedSubjectProvider {

  private static final String USER_SUBJECT_TYPE = "user";

  private final Supplier<Authentication> authenticationSupplier;

  /** Creates a provider backed by the current Spring Security context. */
  public SpringSecurityAuthenticatedSubjectProvider() {
    this(() -> SecurityContextHolder.getContext().getAuthentication());
  }

  /**
   * Creates a provider with an injectable authentication source.
   *
   * @param authenticationSupplier authentication source
   */
  public SpringSecurityAuthenticatedSubjectProvider(
      Supplier<Authentication> authenticationSupplier) {
    this.authenticationSupplier =
        Objects.requireNonNull(authenticationSupplier, "authentication supplier is required");
  }

  @Override
  public Optional<SubjectRef> currentSubject() {
    Authentication authentication = authenticationSupplier.get();
    if (authentication == null
        || !authentication.isAuthenticated()
        || authentication instanceof AnonymousAuthenticationToken) {
      return Optional.empty();
    }
    return Optional.of(new SubjectRef(USER_SUBJECT_TYPE, authentication.getName()));
  }
}
