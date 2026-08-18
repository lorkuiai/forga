package com.luokuiai.forga.core.eval;

/**
 * Stable authorization decision reason.
 */
public enum DecisionReason {
  /** A policy proof matched. */
  ALLOWED,

  /** No policy proof matched. */
  NO_MATCH,

  /** The requested permission is not defined. */
  UNKNOWN_PERMISSION,

  /** Evaluation exceeded a configured bound. */
  LIMIT_EXCEEDED,

  /** Evaluation exceeded its deadline. */
  DEADLINE_EXCEEDED,

  /** Evaluation encountered an active relationship cycle. */
  CYCLE_DETECTED,

  /** A relationship resolver failed. */
  RESOLVER_FAILURE,

  /** A relationship resolver rejected the requested consistency context. */
  CONSISTENCY_CONFLICT,

  /** A listing cursor does not match the current request. */
  INVALID_CURSOR
}
