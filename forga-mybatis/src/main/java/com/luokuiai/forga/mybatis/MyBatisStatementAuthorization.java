package com.luokuiai.forga.mybatis;

import com.luokuiai.forga.core.model.PermissionRef;
import com.luokuiai.forga.query.QueryResource;
import java.util.Objects;

/**
 * Authorization metadata for one MyBatis statement id.
 *
 * @param statementId MyBatis mapped statement id
 * @param resource neutral query resource
 * @param permission requested permission
 * @param boundary typed authorization boundary
 */
public record MyBatisStatementAuthorization(
    String statementId,
    QueryResource resource,
    PermissionRef permission,
    MyBatisAuthorizationBoundary boundary) {

  /**
   * Creates statement authorization metadata.
   *
   * @param statementId MyBatis mapped statement id
   * @param resource neutral query resource
   * @param permission requested permission
   * @param boundary typed authorization boundary
   */
  public MyBatisStatementAuthorization {
    if (statementId == null || statementId.isBlank()) {
      throw new IllegalArgumentException("statementId is required");
    }
    statementId = statementId.trim();
    resource = Objects.requireNonNull(resource, "resource is required");
    permission = Objects.requireNonNull(permission, "permission is required");
    boundary = Objects.requireNonNull(boundary, "boundary is required");
  }
}
