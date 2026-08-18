package com.luokuiai.forga.query;

/**
 * Raised when a constraint generator references a field not allowlisted for a resource.
 */
public final class UnknownQueryFieldException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates an unknown field exception.
   *
   * @param message failure message
   */
  public UnknownQueryFieldException(String message) {
    super(message);
  }
}
