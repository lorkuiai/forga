package com.luokuiai.forga.resolver;

import com.luokuiai.forga.core.model.ObjectRef;
import com.luokuiai.forga.core.model.RelationRef;
import java.util.Objects;

/**
 * Request for subjects related to an object.
 *
 * @param object object to inspect
 * @param relation relation to resolve
 * @param limit maximum number of returned subjects
 * @param context resolver context
 */
public record ForwardRelationshipRequest(
    ObjectRef object, RelationRef relation, int limit, ResolverContext context) {

  /**
   * Creates a forward relationship request with an empty resolver context.
   *
   * @param object object to inspect
   * @param relation relation to resolve
   * @param limit maximum number of returned subjects
   */
  public ForwardRelationshipRequest(ObjectRef object, RelationRef relation, int limit) {
    this(object, relation, limit, ResolverContext.empty());
  }

  /**
   * Creates a forward relationship request.
   *
   * @param object object to inspect
   * @param relation relation to resolve
   * @param limit maximum number of returned subjects
   * @param context resolver context
   */
  public ForwardRelationshipRequest {
    object = Objects.requireNonNull(object, "object is required");
    relation = Objects.requireNonNull(relation, "relation is required");
    limit = ResolverBounds.limit(limit);
    context = Objects.requireNonNull(context, "context is required");
  }
}
