package com.luokuiai.forga.query;

/**
 * Supported typed predicate operators.
 */
public enum PredicateOperator {
  /** Equal comparison. */
  EQUALS,

  /** Non-equal comparison. */
  NOT_EQUALS,

  /** Set membership comparison. */
  IN,

  /** Greater-than comparison. */
  GREATER_THAN,

  /** Greater-than-or-equal comparison. */
  GREATER_THAN_OR_EQUALS,

  /** Less-than comparison. */
  LESS_THAN,

  /** Less-than-or-equal comparison. */
  LESS_THAN_OR_EQUALS
}
