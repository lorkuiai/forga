package com.luokuiai.forga.resolver;

/**
 * Stable resolver failure reason.
 */
public enum ResolverFailureReason {
  /** Resolver exceeded its deadline. */
  TIMEOUT,

  /** Resolver dependency was unavailable. */
  UNAVAILABLE,

  /** Resolver returned malformed data. */
  MALFORMED_RESPONSE,

  /** Resolver observed an incompatible consistency value. */
  CONSISTENCY_CONFLICT
}
