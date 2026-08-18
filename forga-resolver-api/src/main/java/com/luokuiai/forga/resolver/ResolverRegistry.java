package com.luokuiai.forga.resolver;

import com.luokuiai.forga.core.model.AttributeRef;
import com.luokuiai.forga.core.model.RelationRef;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Immutable registry of resolver declarations.
 */
public final class ResolverRegistry {

  private final Map<String, RelationshipResolver> byName;

  /**
   * Creates a resolver registry.
   *
   * @param resolvers resolvers to register
   */
  public ResolverRegistry(List<? extends RelationshipResolver> resolvers) {
    List<RelationshipResolver> copy = List.copyOf(resolvers);
    ensureUniqueNames(copy);
    byName =
        copy.stream()
            .collect(Collectors.toUnmodifiableMap(
                resolver -> resolver.descriptor().name(), Function.identity()));
  }

  /**
   * Returns all resolvers.
   *
   * @return immutable resolvers
   */
  public List<RelationshipResolver> resolvers() {
    return List.copyOf(byName.values());
  }

  /**
   * Finds a resolver that supports forward resolution for a relation.
   *
   * @param relation relation to inspect
   * @return matching resolver
   */
  public Optional<RelationshipResolver> findForward(RelationRef relation) {
    return byName.values().stream()
        .filter(resolver -> resolver.descriptor().supportsForward(relation))
        .findFirst();
  }

  /**
   * Finds a resolver that supports reverse resolution for a relation.
   *
   * @param relation relation to inspect
   * @return matching resolver
   */
  public Optional<RelationshipResolver> findReverse(RelationRef relation) {
    return byName.values().stream()
        .filter(resolver -> resolver.descriptor().supportsReverse(relation))
        .findFirst();
  }

  /**
   * Finds a resolver that supports an attribute.
   *
   * @param attribute attribute to inspect
   * @return matching resolver
   */
  public Optional<RelationshipResolver> findAttribute(AttributeRef attribute) {
    return byName.values().stream()
        .filter(resolver -> resolver.descriptor().supportsAttribute(attribute))
        .findFirst();
  }

  private static void ensureUniqueNames(List<RelationshipResolver> resolvers) {
    List<String> names = new ArrayList<>();
    for (RelationshipResolver resolver : resolvers) {
      String name = resolver.descriptor().name();
      if (names.contains(name)) {
        throw new IllegalArgumentException("duplicate resolver name: " + name);
      }
      names.add(name);
    }
  }
}
