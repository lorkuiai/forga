package com.luokuiai.forga.core.policy;

/**
 * Raised when a policy cannot be compiled safely.
 */
public final class PolicyValidationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a validation exception.
   *
   * @param message validation failure message
   */
  public PolicyValidationException(String message) {
    super(message);
  }
}
