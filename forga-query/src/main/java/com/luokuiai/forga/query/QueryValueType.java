package com.luokuiai.forga.query;

/**
 * Supported bound parameter value types.
 */
public enum QueryValueType {
  /** Text value. */
  STRING,

  /** Numeric value. */
  NUMBER,

  /** Boolean value. */
  BOOLEAN,

  /** Instant or timestamp value. */
  INSTANT,

  /** Opaque caller-defined value. */
  OPAQUE
}
