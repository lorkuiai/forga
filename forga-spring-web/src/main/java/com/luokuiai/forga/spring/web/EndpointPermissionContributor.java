package com.luokuiai.forga.spring.web;

/** Contributes external permission metadata for controllers that hosts cannot annotate. */
@FunctionalInterface
public interface EndpointPermissionContributor {

  /**
   * Adds endpoint permission declarations to the registry.
   *
   * @param registry endpoint permission registry
   */
  void contribute(EndpointPermissionRegistry registry);
}
