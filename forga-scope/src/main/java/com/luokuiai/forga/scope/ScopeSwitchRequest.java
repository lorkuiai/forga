package com.luokuiai.forga.scope;

import com.luokuiai.forga.core.model.PermissionRef;
import com.luokuiai.forga.core.model.SubjectRef;
import java.util.Map;

/**
 * Request to check whether a subject can enter a target scope.
 *
 * @param subject subject requesting the switch
 * @param targetScope target scope
 * @param permission permission that represents scope entry
 * @param attributes request-scoped attributes
 */
public record ScopeSwitchRequest(
    SubjectRef subject,
    ScopeRef targetScope,
    PermissionRef permission,
    Map<com.luokuiai.forga.core.model.AttributeRef, String> attributes) {

  /**
   * Creates a switch request without attributes.
   *
   * @param subject subject requesting the switch
   * @param targetScope target scope
   * @param permission permission that represents scope entry
   */
  public ScopeSwitchRequest(
      SubjectRef subject, ScopeRef targetScope, PermissionRef permission) {
    this(subject, targetScope, permission, Map.of());
  }

  /**
   * Creates a switch request.
   *
   * @param subject subject requesting the switch
   * @param targetScope target scope
   * @param permission permission that represents scope entry
   * @param attributes request-scoped attributes
   */
  public ScopeSwitchRequest {
    if (subject == null) {
      throw new IllegalArgumentException("subject is required");
    }
    if (targetScope == null) {
      throw new IllegalArgumentException("target scope is required");
    }
    if (permission == null) {
      throw new IllegalArgumentException("permission is required");
    }
    attributes = Map.copyOf(attributes);
  }
}
