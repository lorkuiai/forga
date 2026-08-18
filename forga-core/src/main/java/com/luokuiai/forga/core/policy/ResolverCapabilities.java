package com.luokuiai.forga.core.policy;

import com.luokuiai.forga.core.model.CaveatRef;
import com.luokuiai.forga.core.model.RelationRef;
import java.util.Collection;
import java.util.Set;

/**
 * Capabilities required by a policy expression tree.
 *
 * @param relations supported relation names
 * @param caveats supported caveat names
 */
public record ResolverCapabilities(Set<RelationRef> relations, Set<CaveatRef> caveats) {

  /**
   * Creates resolver capabilities.
   *
   * @param relations supported relation names
   * @param caveats supported caveat names
   */
  public ResolverCapabilities {
    relations = Set.copyOf(relations);
    caveats = Set.copyOf(caveats);
  }

  /**
   * Creates capabilities from collections.
   *
   * @param relations supported relation names
   * @param caveats supported caveat names
   * @return resolver capabilities
   */
  public static ResolverCapabilities of(
      Collection<RelationRef> relations, Collection<CaveatRef> caveats) {
    return new ResolverCapabilities(Set.copyOf(relations), Set.copyOf(caveats));
  }

  boolean supports(RelationRef relation) {
    return relations.contains(relation);
  }

  boolean supports(CaveatRef caveat) {
    return caveats.contains(caveat);
  }
}
