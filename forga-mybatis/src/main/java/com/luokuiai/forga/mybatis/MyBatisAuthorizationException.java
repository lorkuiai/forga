package com.luokuiai.forga.mybatis;

/**
 * Raised when enabled MyBatis authorization fails closed.
 */
public final class MyBatisAuthorizationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates an authorization exception.
   *
   * @param message failure message
   */
  public MyBatisAuthorizationException(String message) {
    super(message);
  }
}
