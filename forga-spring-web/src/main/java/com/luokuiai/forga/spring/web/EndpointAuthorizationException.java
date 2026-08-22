package com.luokuiai.forga.spring.web;

import java.util.Objects;
import java.util.Optional;

/** Raised when endpoint permission metadata or authorization fails closed. */
public final class EndpointAuthorizationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /** Stable denial reason. */
  private final String reason;

  /** Host decision when endpoint authorization reached the configured authorizer. */
  private final Optional<EndpointAuthorizationDecision> decision;

  private EndpointAuthorizationException(
      String reason, Optional<EndpointAuthorizationDecision> decision) {
    super("endpoint authorization denied: " + reason);
    this.reason = reason;
    this.decision = decision;
  }

  /**
   * Creates an unresolved-endpoint denial.
   *
   * @return unresolved-endpoint exception
   */
  public static EndpointAuthorizationException unresolved() {
    return new EndpointAuthorizationException("ENDPOINT_PERMISSION_UNRESOLVED", Optional.empty());
  }

  /**
   * Creates a conflicting endpoint metadata denial.
   *
   * @return conflicting-metadata exception
   */
  public static EndpointAuthorizationException conflictingMetadata() {
    return new EndpointAuthorizationException("ENDPOINT_PERMISSION_CONFLICT", Optional.empty());
  }

  /**
   * Creates an authorizer denial.
   *
   * @param decision denied decision
   * @return denied endpoint exception
   */
  public static EndpointAuthorizationException denied(EndpointAuthorizationDecision decision) {
    EndpointAuthorizationDecision checked =
        Objects.requireNonNull(decision, "decision is required");
    return new EndpointAuthorizationException(checked.reason(), Optional.of(checked));
  }

  /**
   * Returns the stable denial reason.
   *
   * @return denial reason
   */
  public String reason() {
    return reason;
  }

  /**
   * Returns the authorizer decision when authorization reached the host adapter.
   *
   * @return denied decision when available
   */
  public Optional<EndpointAuthorizationDecision> decision() {
    return decision;
  }
}
