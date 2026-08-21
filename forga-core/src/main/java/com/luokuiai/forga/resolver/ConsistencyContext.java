package com.luokuiai.forga.resolver;

import com.luokuiai.forga.core.model.ConsistencyToken;
import java.util.Optional;

/**
 * Opaque consistency state propagated across resolver calls.
 *
 * @param token optional consistency token
 */
public record ConsistencyContext(Optional<ConsistencyToken> token) {

  /**
   * Creates a consistency context.
   *
   * @param token optional consistency token
   */
  public ConsistencyContext {
    token = token == null ? Optional.empty() : token;
  }

  /**
   * Creates an empty consistency context.
   *
   * @return empty consistency context
   */
  public static ConsistencyContext empty() {
    return new ConsistencyContext(Optional.empty());
  }

  /**
   * Creates a consistency context with a token.
   *
   * @param token consistency token
   * @return consistency context
   */
  public static ConsistencyContext of(ConsistencyToken token) {
    return new ConsistencyContext(Optional.of(token));
  }
}
