package com.luokuiai.forga.resolver;

import java.util.Objects;

/**
 * Structured resolver failure.
 *
 * @param reason stable failure reason
 * @param message safe failure message
 */
public record ResolverFailure(ResolverFailureReason reason, String message) {

  /**
   * Creates a resolver failure.
   *
   * @param reason stable failure reason
   * @param message safe failure message
   */
  public ResolverFailure {
    reason = Objects.requireNonNull(reason, "reason is required");
    if (message == null || message.isBlank()) {
      throw new IllegalArgumentException("message is required");
    }
    message = message.trim();
  }
}
