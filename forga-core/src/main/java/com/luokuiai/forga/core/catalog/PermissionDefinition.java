package com.luokuiai.forga.core.catalog;

import com.luokuiai.forga.core.model.PermissionRef;
import java.util.Objects;

/**
 * Host-defined permission metadata suitable for persistence and administration.
 *
 * @param permission stable permission reference
 * @param displayName host-facing display name
 * @param module host-defined owning module
 */
public record PermissionDefinition(
    PermissionRef permission, String displayName, String module) {

  /**
   * Creates a permission definition.
   *
   * @param permission stable permission reference
   * @param displayName host-facing display name
   * @param module host-defined owning module
   */
  public PermissionDefinition {
    permission = Objects.requireNonNull(permission, "permission is required");
    displayName = required("display name", displayName);
    module = required("module", module);
  }

  private static String required(String field, String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(field + " is required");
    }
    return value.trim();
  }
}
