package com.luokuiai.forga.core.catalog;

import java.util.Collection;

/** Contributes host-owned permission definitions to a catalog. */
@FunctionalInterface
public interface PermissionCatalogContributor {

  /**
   * Returns permission definitions owned by one host module.
   *
   * @return contributed permission definitions
   */
  Collection<PermissionDefinition> definitions();
}
