package com.luokuiai.forga.resolver;

import com.luokuiai.forga.core.model.AttributeRef;
import com.luokuiai.forga.core.model.ObjectRef;
import java.util.List;
import java.util.Objects;

/**
 * Request for attributes attached to an object.
 *
 * @param object object whose attributes are requested
 * @param attributes immutable requested attributes
 * @param context resolver context
 */
public record AttributeResolutionRequest(
    ObjectRef object, List<AttributeRef> attributes, ResolverContext context) {

  /**
   * Creates an attribute resolution request with an empty resolver context.
   *
   * @param object object whose attributes are requested
   * @param attributes requested attributes
   */
  public AttributeResolutionRequest(ObjectRef object, List<AttributeRef> attributes) {
    this(object, attributes, ResolverContext.empty());
  }

  /**
   * Creates an attribute resolution request.
   *
   * @param object object whose attributes are requested
   * @param attributes requested attributes
   * @param context resolver context
   */
  public AttributeResolutionRequest {
    object = Objects.requireNonNull(object, "object is required");
    attributes = List.copyOf(attributes);
    if (attributes.isEmpty()) {
      throw new IllegalArgumentException("at least one attribute is required");
    }
    context = Objects.requireNonNull(context, "context is required");
  }
}
