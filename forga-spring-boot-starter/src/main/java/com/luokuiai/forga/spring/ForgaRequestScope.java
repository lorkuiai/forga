package com.luokuiai.forga.spring;

import java.util.Optional;

/**
 * Thread-local request scope used by synchronous framework integrations.
 */
public final class ForgaRequestScope implements AutoCloseable {

  private static final ThreadLocal<ForgaRequestContext> CURRENT = new ThreadLocal<>();

  private ForgaRequestScope(ForgaRequestContext context) {
    CURRENT.set(context);
  }

  /**
   * Opens a request scope.
   *
   * @param context request context
   * @return scope that clears context on close
   */
  public static ForgaRequestScope open(ForgaRequestContext context) {
    return new ForgaRequestScope(context);
  }

  /**
   * Returns the current request context.
   *
   * @return current context
   */
  public static Optional<ForgaRequestContext> current() {
    return Optional.ofNullable(CURRENT.get());
  }

  /**
   * Clears the current request context.
   */
  @Override
  public void close() {
    CURRENT.remove();
  }
}
