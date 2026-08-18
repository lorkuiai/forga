package com.luokuiai.forga.core.eval;

import com.luokuiai.forga.core.model.AttributeRef;
import com.luokuiai.forga.core.model.ConsistencyToken;
import com.luokuiai.forga.core.model.ObjectRef;
import com.luokuiai.forga.core.model.PermissionRef;
import com.luokuiai.forga.core.model.SubjectRef;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Request to list objects authorized for a subject and permission.
 *
 * @param objectType caller-defined object type to return
 * @param permission requested permission
 * @param subject subject to list for
 * @param pageSize maximum returned objects
 * @param cursor optional continuation cursor
 * @param consistency optional caller-provided consistency token
 * @param attributes request-scoped attributes
 */
public record ListObjectsRequest(
    String objectType,
    PermissionRef permission,
    SubjectRef subject,
    int pageSize,
    Optional<ListObjectsCursor> cursor,
    Optional<ConsistencyToken> consistency,
    Map<AttributeRef, String> attributes) {

  /**
   * Creates a listing request without cursor or attributes.
   *
   * @param objectType caller-defined object type to return
   * @param permission requested permission
   * @param subject subject to list for
   * @param pageSize maximum returned objects
   */
  public ListObjectsRequest(
      String objectType, PermissionRef permission, SubjectRef subject, int pageSize) {
    this(objectType, permission, subject, pageSize, Optional.empty(), Optional.empty(), Map.of());
  }

  /**
   * Creates a listing request with a cursor and attributes.
   *
   * @param objectType caller-defined object type to return
   * @param permission requested permission
   * @param subject subject to list for
   * @param pageSize maximum returned objects
   * @param cursor optional continuation cursor
   * @param attributes request-scoped attributes
   */
  public ListObjectsRequest(
      String objectType,
      PermissionRef permission,
      SubjectRef subject,
      int pageSize,
      Optional<ListObjectsCursor> cursor,
      Map<AttributeRef, String> attributes) {
    this(objectType, permission, subject, pageSize, cursor, Optional.empty(), attributes);
  }

  /**
   * Creates a listing request.
   *
   * @param objectType caller-defined object type to return
   * @param permission requested permission
   * @param subject subject to list for
   * @param pageSize maximum returned objects
   * @param cursor optional continuation cursor
   * @param consistency optional caller-provided consistency token
   * @param attributes request-scoped attributes
   */
  public ListObjectsRequest {
    objectType = new ObjectRef(objectType, "listing").type();
    permission = Objects.requireNonNull(permission, "permission is required");
    subject = Objects.requireNonNull(subject, "subject is required");
    if (pageSize < 1) {
      throw new IllegalArgumentException("pageSize must be positive");
    }
    cursor = cursor == null ? Optional.empty() : cursor;
    consistency = consistency == null ? Optional.empty() : consistency;
    attributes = Map.copyOf(attributes);
  }
}
