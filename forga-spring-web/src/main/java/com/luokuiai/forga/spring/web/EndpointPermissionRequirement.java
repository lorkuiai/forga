package com.luokuiai.forga.spring.web;

import com.luokuiai.forga.core.model.PermissionRef;
import java.util.Objects;
import java.util.Optional;

/** Resolved permission metadata for one Spring Web endpoint. */
public final class EndpointPermissionRequirement {

  private final Optional<PermissionRef> permission;

  private EndpointPermissionRequirement(Optional<PermissionRef> permission) {
    this.permission = permission;
  }

  /**
   * Creates a required-permission result.
   *
   * @param permission required permission
   * @return required-permission result
   */
  public static EndpointPermissionRequirement required(PermissionRef permission) {
    return new EndpointPermissionRequirement(
        Optional.of(Objects.requireNonNull(permission, "permission is required")));
  }

  /**
   * Creates an explicit permit-all result.
   *
   * @return permit-all result
   */
  public static EndpointPermissionRequirement permitAll() {
    return new EndpointPermissionRequirement(Optional.empty());
  }

  /**
   * Returns the required permission, or empty for permit-all.
   *
   * @return required permission
   */
  public Optional<PermissionRef> permission() {
    return permission;
  }

  /**
   * Returns whether the endpoint explicitly permits all callers.
   *
   * @return true for permit-all metadata
   */
  public boolean isPermitAll() {
    return permission.isEmpty();
  }
}
