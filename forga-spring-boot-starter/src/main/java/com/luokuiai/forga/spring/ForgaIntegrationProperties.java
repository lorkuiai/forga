package com.luokuiai.forga.spring;

import com.luokuiai.forga.core.eval.EvaluationLimits;
import java.util.Objects;

/**
 * Runtime integration properties.
 *
 * @param enabled whether integration is enabled
 * @param limits evaluation limits
 */
public record ForgaIntegrationProperties(boolean enabled, EvaluationLimits limits) {

  /**
   * Creates integration properties.
   *
   * @param enabled whether integration is enabled
   * @param limits evaluation limits
   */
  public ForgaIntegrationProperties {
    limits = Objects.requireNonNull(limits, "limits are required");
  }

  /**
   * Returns disabled defaults.
   *
   * @return disabled properties
   */
  public static ForgaIntegrationProperties disabledDefaults() {
    return new ForgaIntegrationProperties(false, EvaluationLimits.defaults());
  }

  /**
   * Returns enabled defaults.
   *
   * @return enabled properties
   */
  public static ForgaIntegrationProperties enabledDefaults() {
    return new ForgaIntegrationProperties(true, EvaluationLimits.defaults());
  }
}
