package com.luokuiai.forga.spring;

/**
 * Raised when runtime integration cannot be assembled safely.
 */
public final class ForgaRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a runtime exception.
   *
   * @param message failure message
   */
  public ForgaRuntimeException(String message) {
    super(message);
  }
}
