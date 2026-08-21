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

  private final Supplier<Authentication> authenticationSupplier;

  private final String subjectType;

  /**
   * Creates a provider backed by the current Spring Security context.
   *
   * @param subjectType caller-defined Forga subject type
   */
  public SpringSecurityAuthenticatedSubjectProvider(String subjectType) {
    this(() -> SecurityContextHolder.getContext().getAuthentication(), subjectType);
  }

  /**
   * Creates a provider with an injectable authentication source.
   *
   * @param authenticationSupplier authentication source
   * @param subjectType caller-defined Forga subject type
   */
  public SpringSecurityAuthenticatedSubjectProvider(
      Supplier<Authentication> authenticationSupplier, String subjectType) {
    this.authenticationSupplier =
        Objects.requireNonNull(authenticationSupplier, "authentication supplier is required");
    this.subjectType = validateSubjectType(subjectType);
  }

  @Override
  public Optional<SubjectRef> currentSubject() {
    Authentication authentication = authenticationSupplier.get();
    if (authentication == null
        || !authentication.isAuthenticated()
        || authentication instanceof AnonymousAuthenticationToken) {
      return Optional.empty();
    }
    return Optional.of(new SubjectRef(subjectType, authentication.getName()));
  }

  private static String validateSubjectType(String subjectType) {
    return new SubjectRef(subjectType, "validation").type();
  }
}
