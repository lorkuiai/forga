package com.luokuiai.forga.core.eval;

import java.util.Objects;

/**
 * Runtime failure raised by a relationship lookup implementation.
 */
public final class RelationshipLookupException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final DecisionReason reason;

  /**
   * Creates a lookup failure.
   *
   * @param reason fail-closed decision reason
   * @param message failure message
   */
  public RelationshipLookupException(DecisionReason reason, String message) {
    super(message);
    if (reason != DecisionReason.RESOLVER_FAILURE
        && reason != DecisionReason.CONSISTENCY_CONFLICT) {
      throw new IllegalArgumentException("reason must describe a resolver failure");
    }
    this.reason = Objects.requireNonNull(reason, "reason is required");
  }

  /**
   * Returns the fail-closed decision reason.
   *
   * @return decision reason
   */
  public DecisionReason reason() {
    return reason;
  }
}
