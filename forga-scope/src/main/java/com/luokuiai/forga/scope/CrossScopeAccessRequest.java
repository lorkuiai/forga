package com.luokuiai.forga.scope;

import com.luokuiai.forga.core.model.AttributeRef;
import com.luokuiai.forga.core.model.ObjectRef;
import com.luokuiai.forga.core.model.PermissionRef;
import com.luokuiai.forga.core.model.SubjectRef;
import java.util.Map;

/**
 * Request for an explicit grant from the active scope to an object owned by another scope.
 *
 * @param activeScope scope selected for the current request
 * @param objectScope scope that owns the protected object
 * @param object protected object
 * @param permission requested permission
 * @param subject subject being authorized
 * @param attributes immutable request-scoped attributes
 */
public record CrossScopeAccessRequest(
    ScopeRef activeScope,
    ScopeRef objectScope,
    ObjectRef object,
    PermissionRef permission,
    SubjectRef subject,
    Map<AttributeRef, String> attributes) {

  /**
   * Creates a cross-scope access request.
   *
   * @param activeScope scope selected for the current request
   * @param objectScope scope that owns the protected object
   * @param object protected object
   * @param permission requested permission
   * @param subject subject being authorized
   * @param attributes request-scoped attributes
   */
  public CrossScopeAccessRequest {
    if (activeScope == null) {
      throw new IllegalArgumentException("active scope is required");
    }
    if (objectScope == null) {
      throw new IllegalArgumentException("object scope is required");
    }
    if (activeScope.equals(objectScope)) {
      throw new IllegalArgumentException("active scope and object scope must differ");
    }
    if (object == null) {
      throw new IllegalArgumentException("object is required");
    }
    if (permission == null) {
      throw new IllegalArgumentException("permission is required");
    }
    if (subject == null) {
      throw new IllegalArgumentException("subject is required");
    }
    if (attributes == null) {
      throw new IllegalArgumentException("attributes are required");
    }
    attributes = Map.copyOf(attributes);
  }
}
