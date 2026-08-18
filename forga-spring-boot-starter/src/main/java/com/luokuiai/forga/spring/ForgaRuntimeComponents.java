package com.luokuiai.forga.spring;

import com.luokuiai.forga.core.eval.EvaluationLimits;
import com.luokuiai.forga.core.policy.CompiledPolicy;
import com.luokuiai.forga.mybatis.MyBatisConstraintTranslator;
import com.luokuiai.forga.resolver.ResolverRegistry;
import java.util.Objects;

/**
 * Components assembled when the integration is enabled.
 *
 * @param policy compiled policy
 * @param resolvers resolver registry
 * @param limits evaluation limits
 * @param translator query constraint translator
 */
public record ForgaRuntimeComponents(
    CompiledPolicy policy,
    ResolverRegistry resolvers,
    EvaluationLimits limits,
    MyBatisConstraintTranslator translator) {

  /**
   * Creates runtime components.
   *
   * @param policy compiled policy
   * @param resolvers resolver registry
   * @param limits evaluation limits
   * @param translator query constraint translator
   */
  public ForgaRuntimeComponents {
    policy = Objects.requireNonNull(policy, "policy is required");
    resolvers = Objects.requireNonNull(resolvers, "resolvers are required");
    limits = Objects.requireNonNull(limits, "limits are required");
    translator = Objects.requireNonNull(translator, "translator is required");
  }
}
