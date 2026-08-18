package com.luokuiai.forga.core.eval;

import java.util.Objects;
import java.util.List;

/**
 * Authorization decision for a check request.
 *
 * @param request evaluated request
 * @param allowed whether access is allowed
 * @param reason stable decision reason
 * @param proof immutable proof steps for allowed decisions
 */
public record CheckDecision(
    CheckRequest request, boolean allowed, DecisionReason reason, List<ProofStep> proof) {

  /**
   * Creates a decision without proof steps.
   *
   * @param request evaluated request
   * @param allowed whether access is allowed
   * @param reason stable decision reason
   */
  public CheckDecision(CheckRequest request, boolean allowed, DecisionReason reason) {
    this(request, allowed, reason, List.of());
  }

  /**
   * Creates a check decision.
   *
   * @param request evaluated request
   * @param allowed whether access is allowed
   * @param reason stable decision reason
   * @param proof proof steps for allowed decisions
   */
  public CheckDecision {
    request = Objects.requireNonNull(request, "request is required");
    reason = Objects.requireNonNull(reason, "reason is required");
    proof = List.copyOf(proof);
  }
}
