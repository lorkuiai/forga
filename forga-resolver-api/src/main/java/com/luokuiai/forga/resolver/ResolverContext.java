package com.luokuiai.forga.resolver;

import java.util.Objects;
import java.util.Optional;

/**
 * Common context sent with resolver requests.
 *
 * @param consistency consistency context
 * @param deadline optional deadline
 */
public record ResolverContext(
    ConsistencyContext consistency, Optional<ResolverDeadline> deadline) {

  /**
   * Creates a resolver context.
   *
   * @param consistency consistency context
   * @param deadline optional deadline
   */
  public ResolverContext {
    consistency = Objects.requireNonNull(consistency, "consistency is required");
    deadline = deadline == null ? Optional.empty() : deadline;
  }

  /**
   * Creates a context without a token or deadline.
   *
   * @return empty resolver context
   */
  public static ResolverContext empty() {
    return new ResolverContext(ConsistencyContext.empty(), Optional.empty());
  }
}
