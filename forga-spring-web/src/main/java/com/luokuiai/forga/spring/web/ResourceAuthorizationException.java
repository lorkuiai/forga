package com.luokuiai.forga.spring.web;

/**
 * Default exception thrown when a resource-code authorization check is denied.
 */
public class ResourceAuthorizationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /** Denied resource authorization decision. */
  private final ResourceAuthorizationDecision decision;

  /**
   * Creates a denial exception.
   *
   * @param decision denied decision
   */
  public ResourceAuthorizationException(ResourceAuthorizationDecision decision) {
    super("resource authorization denied: " + decision.resourceCode());
    this.decision = decision;
  }

  /**
   * Returns the denied decision.
   *
   * @return denied decision
   */
  public ResourceAuthorizationDecision decision() {
    return decision;
  }
}
