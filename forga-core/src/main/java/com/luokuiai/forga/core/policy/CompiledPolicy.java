package com.luokuiai.forga.core.policy;

import java.util.Objects;

/**
 * Validated immutable policy with a stable fingerprint.
 *
 * @param definition source policy definition
 * @param fingerprint stable fingerprint for cursor and cache binding
 */
public record CompiledPolicy(PolicyDefinition definition, String fingerprint) {

  /**
   * Creates a compiled policy.
   *
   * @param definition source policy definition
   * @param fingerprint stable fingerprint for cursor and cache binding
   */
  public CompiledPolicy {
    definition = Objects.requireNonNull(definition, "definition is required");
    fingerprint = Objects.requireNonNull(fingerprint, "fingerprint is required");
    if (fingerprint.isBlank()) {
      throw new IllegalArgumentException("fingerprint is required");
    }
  }
}
