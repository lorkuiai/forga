package com.luokuiai.forga.scope;

import com.luokuiai.forga.core.model.AttributeRef;
import com.luokuiai.forga.core.model.ObjectRef;
import com.luokuiai.forga.core.model.PermissionRef;
import com.luokuiai.forga.core.model.SubjectRef;
import java.util.Optional;
import java.util.Map;

/**
 * Request to check a permission while bound to an active scope.
 *
 * @param object protected object
 * @param permission requested permission
 * @param subject subject to authorize
 * @param activeScope active scope required by the request
 * @param attributes request-scoped attributes
 */
public record ScopedPermissionRequest(
    ObjectRef object,
    PermissionRef permission,
    SubjectRef subject,
    Optional<ActiveScope> activeScope,
    Map<AttributeRef, String> attributes) {

  /**
   * Creates a scoped permission request.
   *
   * @param object protected object
   * @param permission requested permission
   * @param scopedSubject subject and active scope
   */
  public ScopedPermissionRequest(
      ObjectRef object, PermissionRef permission, ScopedSubject scopedSubject) {
    this(object, permission, scopedSubject.subject(), scopedSubject.activeScope(), Map.of());
  }

  /**
   * Creates a scoped permission request.
   *
   * @param object protected object
   * @param permission requested permission
   * @param subject subject to authorize
   * @param activeScope active scope required by the request
   * @param attributes request-scoped attributes
   */
  public ScopedPermissionRequest {
    if (object == null) {
      throw new IllegalArgumentException("object is required");
    }
    if (permission == null) {
      throw new IllegalArgumentException("permission is required");
    }
    if (subject == null) {
      throw new IllegalArgumentException("subject is required");
    }
    activeScope = activeScope == null ? Optional.empty() : activeScope;
    attributes = Map.copyOf(attributes);
  }
}
