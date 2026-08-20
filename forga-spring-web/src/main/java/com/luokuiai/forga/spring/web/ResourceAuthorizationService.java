package com.luokuiai.forga.spring.web;

import java.util.Objects;

/**
 * Programmatic facade for host-defined resource-code authorization.
 */
public final class ResourceAuthorizationService {

  private final ResourceCheckAdapter adapter;

  /**
   * Creates a resource authorization service.
   *
   * @param adapter host-owned resource check adapter
   */
  public ResourceAuthorizationService(ResourceCheckAdapter adapter) {
    this.adapter = Objects.requireNonNull(adapter, "adapter is required");
  }

  /**
   * Requires the current caller to have a resource permission.
   *
   * @param resourceCode host-defined resource permission code
   */
  public void requireResource(String resourceCode) {
    requireResource(new ResourceInvocation(resourceCode));
  }

  /**
   * Requires the current caller to have a resource permission.
   *
   * @param invocation resource invocation context
   */
  public void requireResource(ResourceInvocation invocation) {
    ResourceAuthorizationDecision decision = authorize(invocation);
    if (!decision.allowed()) {
      throw new ResourceAuthorizationException(decision);
    }
  }

  /**
   * Checks whether the current caller has a resource permission.
   *
   * @param resourceCode host-defined resource permission code
   * @return true when allowed
   */
  public boolean hasResource(String resourceCode) {
    return authorize(new ResourceInvocation(resourceCode)).allowed();
  }

  /**
   * Checks whether the current caller has a resource permission.
   *
   * @param invocation resource invocation context
   * @return authorization decision
   */
  public ResourceAuthorizationDecision authorize(ResourceInvocation invocation) {
    ResourceInvocation checkedInvocation =
        Objects.requireNonNull(invocation, "invocation is required");
    ResourceAuthorizationDecision decision = adapter.check(checkedInvocation);
    if (decision == null) {
      return ResourceAuthorizationDecision.denied(
          checkedInvocation.resourceCode(), "ADAPTER_RETURNED_NULL");
    }
    return decision;
  }
}
