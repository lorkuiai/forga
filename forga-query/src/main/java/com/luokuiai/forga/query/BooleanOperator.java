package com.luokuiai.forga.query;

/**
 * Boolean composition operators.
 */
public enum BooleanOperator {
  /** Every child must match. */
  AND,

  /** Any child may match. */
  OR,

  /** The child must not match. */
  NOT
}
