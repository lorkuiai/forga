package com.luokuiai.forga.core.eval;

import com.luokuiai.forga.core.model.AttributeRef;
import com.luokuiai.forga.core.model.ObjectRef;
import com.luokuiai.forga.core.model.PermissionRef;
import com.luokuiai.forga.core.model.SubjectRef;
import java.util.Map;
import java.util.Objects;

/**
 * Request to check whether a subject has a permission on an object.
 *
 * @param object protected object
 * @param permission requested permission
 * @param subject subject to check
 * @param attributes request-scoped attributes
 */
public record CheckRequest(
    ObjectRef object,
    PermissionRef permission,
    SubjectRef subject,
    Map<AttributeRef, String> attributes) {

  /**
   * Creates a check request without request attributes.
   *
   * @param object protected object
   * @param permission requested permission
   * @param subject subject to check
   */
  public CheckRequest(ObjectRef object, PermissionRef permission, SubjectRef subject) {
    this(object, permission, subject, Map.of());
  }

  /**
   * Creates a check request.
   *
   * @param object protected object
   * @param permission requested permission
   * @param subject subject to check
   * @param attributes request-scoped attributes
   */
  public CheckRequest {
    object = Objects.requireNonNull(object, "object is required");
    permission = Objects.requireNonNull(permission, "permission is required");
    subject = Objects.requireNonNull(subject, "subject is required");
    attributes = Map.copyOf(attributes);
  }
}
