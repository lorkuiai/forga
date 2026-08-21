package com.luokuiai.forga.resolver;

import java.time.Instant;
import java.util.Objects;

/**
 * Deadline applied to resolver work.
 *
 * @param expiresAt instant when resolver work must stop
 */
public record ResolverDeadline(Instant expiresAt) {

  /**
   * Creates a resolver deadline.
   *
   * @param expiresAt instant when resolver work must stop
   */
  public ResolverDeadline {
    expiresAt = Objects.requireNonNull(expiresAt, "expiresAt is required");
  }
}
