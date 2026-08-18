package com.luokuiai.forga.core.eval;

import com.luokuiai.forga.core.model.CaveatRef;

/**
 * Evaluates caveats against request-scoped attributes.
 */
@FunctionalInterface
public interface CaveatEvaluator {

  /**
   * Evaluates a caveat.
   *
   * @param caveat caveat to evaluate
   * @param request check request
   * @return true when caveat passes
   */
  boolean evaluate(CaveatRef caveat, CheckRequest request);
}
