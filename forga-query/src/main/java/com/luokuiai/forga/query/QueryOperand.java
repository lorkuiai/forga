package com.luokuiai.forga.query;

/**
 * Operand used inside typed predicates.
 */
public sealed interface QueryOperand permits QueryFieldOperand, QueryParameter {
}
