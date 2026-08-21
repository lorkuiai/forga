package com.luokuiai.forga.resolver;

import com.luokuiai.forga.core.model.AttributeRef;
import com.luokuiai.forga.core.model.RelationRef;
import java.util.Set;

/**
 * Declared capabilities for a resolver.
 *
 * @param name stable resolver name
 * @param forwardRelations relations supported by forward resolution
 * @param reverseRelations relations supported by reverse resolution
 * @param attributes attributes supported by attribute resolution
 */
public record ResolverDescriptor(
    String name,
    Set<RelationRef> forwardRelations,
    Set<RelationRef> reverseRelations,
    Set<AttributeRef> attributes) {

  /**
   * Creates a resolver descriptor.
   *
   * @param name stable resolver name
   * @param forwardRelations relations supported by forward resolution
   * @param reverseRelations relations supported by reverse resolution
   * @param attributes attributes supported by attribute resolution
   */
  public ResolverDescriptor {
    name = requiredName(name);
    forwardRelations = Set.copyOf(forwardRelations);
    reverseRelations = Set.copyOf(reverseRelations);
    attributes = Set.copyOf(attributes);
  }

  /**
   * Returns whether this resolver supports forward resolution for a relation.
   *
   * @param relation relation to inspect
   * @return true when supported
   */
  public boolean supportsForward(RelationRef relation) {
    return forwardRelations.contains(relation);
  }

  /**
   * Returns whether this resolver supports reverse resolution for a relation.
   *
   * @param relation relation to inspect
   * @return true when supported
   */
  public boolean supportsReverse(RelationRef relation) {
    return reverseRelations.contains(relation);
  }

  /**
   * Returns whether this resolver supports an attribute.
   *
   * @param attribute attribute to inspect
   * @return true when supported
   */
  public boolean supportsAttribute(AttributeRef attribute) {
    return attributes.contains(attribute);
  }

  private static String requiredName(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("name is required");
    }
    return value.trim();
  }
}
