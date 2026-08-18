package com.luokuiai.forga.spring;

import com.luokuiai.forga.core.model.RelationRef;
import com.luokuiai.forga.core.policy.CaveatExpression;
import com.luokuiai.forga.core.policy.CompiledPolicy;
import com.luokuiai.forga.core.policy.ExclusionExpression;
import com.luokuiai.forga.core.policy.IntersectionExpression;
import com.luokuiai.forga.core.policy.PermissionExpression;
import com.luokuiai.forga.core.policy.RelationExpression;
import com.luokuiai.forga.core.policy.TraversalExpression;
import com.luokuiai.forga.core.policy.UnionExpression;
import com.luokuiai.forga.mybatis.MyBatisConstraintTranslator;
import com.luokuiai.forga.mybatis.MyBatisResourceMapping;
import com.luokuiai.forga.query.QueryResource;
import com.luokuiai.forga.resolver.ResolverRegistry;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Assembles integration components after opt-in configuration validation.
 */
public final class ForgaRuntimeAssembler {

  private ForgaRuntimeAssembler() {
  }

  /**
   * Assembles enabled runtime components or returns empty when disabled.
   *
   * @param properties integration properties
   * @param policy compiled policy
   * @param resolvers resolver registry
   * @param mappings MyBatis resource mappings
   * @return assembled components when enabled
   */
  public static Optional<ForgaRuntimeComponents> assemble(
      ForgaIntegrationProperties properties,
      CompiledPolicy policy,
      ResolverRegistry resolvers,
      Map<QueryResource, MyBatisResourceMapping> mappings) {
    Objects.requireNonNull(properties, "properties are required");
    if (!properties.enabled()) {
      return Optional.empty();
    }
    if (policy == null) {
      throw new ForgaRuntimeException("enabled integration requires a policy");
    }
    if (resolvers == null) {
      throw new ForgaRuntimeException("enabled integration requires resolver registry");
    }
    validateResolvers(policy, resolvers);
    return Optional.of(
        new ForgaRuntimeComponents(
            policy, resolvers, properties.limits(), new MyBatisConstraintTranslator(mappings)));
  }

  private static void validateResolvers(CompiledPolicy policy, ResolverRegistry resolvers) {
    for (RelationRef relation : relations(policy)) {
      if (resolvers.findForward(relation).isEmpty()) {
        throw new ForgaRuntimeException(
            "missing forward resolver for relation: " + relation.name());
      }
      if (resolvers.findReverse(relation).isEmpty()) {
        throw new ForgaRuntimeException(
            "missing reverse resolver for relation: " + relation.name());
      }
    }
  }

  private static Set<RelationRef> relations(CompiledPolicy policy) {
    Set<RelationRef> relations = new LinkedHashSet<>();
    policy.definition().permissions().values()
        .forEach(expression -> collect(expression, relations));
    return relations;
  }

  private static void collect(PermissionExpression expression, Set<RelationRef> relations) {
    if (expression instanceof RelationExpression relationExpression) {
      relations.add(relationExpression.relation());
    } else if (expression instanceof UnionExpression unionExpression) {
      unionExpression.expressions().forEach(branch -> collect(branch, relations));
    } else if (expression instanceof IntersectionExpression intersectionExpression) {
      intersectionExpression.expressions().forEach(branch -> collect(branch, relations));
    } else if (expression instanceof ExclusionExpression exclusionExpression) {
      collect(exclusionExpression.base(), relations);
      collect(exclusionExpression.excluded(), relations);
    } else if (expression instanceof TraversalExpression traversalExpression) {
      relations.add(traversalExpression.relation());
      collect(traversalExpression.expression(), relations);
    } else if (expression instanceof CaveatExpression caveatExpression) {
      collect(caveatExpression.expression(), relations);
    }
  }
}
