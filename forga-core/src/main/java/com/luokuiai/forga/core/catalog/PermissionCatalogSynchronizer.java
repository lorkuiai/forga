package com.luokuiai.forga.core.catalog;

/** Host-owned persistence boundary for an assembled permission catalog. */
@FunctionalInterface
public interface PermissionCatalogSynchronizer {

  /**
   * Synchronizes a validated immutable permission catalog using host persistence rules.
   *
   * @param catalog permission catalog
   */
  void synchronize(PermissionCatalog catalog);
}
