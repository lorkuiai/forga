package com.luokuiai.forga.core.catalog;

import com.luokuiai.forga.core.model.PermissionRef;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/** Immutable catalog of host-defined permission metadata. */
public final class PermissionCatalog {

  private final Map<PermissionRef, PermissionDefinition> definitions;

  /**
   * Creates a permission catalog.
   *
   * @param definitions permission definitions
   */
  public PermissionCatalog(Collection<PermissionDefinition> definitions) {
    Objects.requireNonNull(definitions, "definitions are required");
    Map<PermissionRef, PermissionDefinition> indexed = new LinkedHashMap<>();
    for (PermissionDefinition definition : definitions) {
      PermissionDefinition checked = Objects.requireNonNull(definition, "definition is required");
      PermissionDefinition duplicate = indexed.putIfAbsent(checked.permission(), checked);
      if (duplicate != null) {
        throw new IllegalArgumentException(
            "duplicate permission: " + checked.permission().name());
      }
    }
    this.definitions = Map.copyOf(indexed);
  }

  /**
   * Assembles a catalog from independent contributors.
   *
   * @param contributors permission contributors
   * @return assembled permission catalog
   */
  public static PermissionCatalog fromContributors(
      Collection<? extends PermissionCatalogContributor> contributors) {
    Objects.requireNonNull(contributors, "contributors are required");
    List<PermissionDefinition> definitions = new ArrayList<>();
    for (PermissionCatalogContributor contributor : contributors) {
      PermissionCatalogContributor checked =
          Objects.requireNonNull(contributor, "contributor is required");
      Collection<PermissionDefinition> contributed =
          Objects.requireNonNull(checked.definitions(), "contributed definitions are required");
      definitions.addAll(contributed);
    }
    return new PermissionCatalog(definitions);
  }

  /**
   * Returns definitions in deterministic permission-name order.
   *
   * @return immutable permission definitions
   */
  public List<PermissionDefinition> definitions() {
    return definitions.values().stream()
        .sorted(java.util.Comparator.comparing(definition -> definition.permission().name()))
        .toList();
  }

  /**
   * Finds one permission definition.
   *
   * @param permission permission reference
   * @return matching definition when present
   */
  public Optional<PermissionDefinition> find(PermissionRef permission) {
    PermissionRef checked = Objects.requireNonNull(permission, "permission is required");
    return Optional.ofNullable(definitions.get(checked));
  }
}
