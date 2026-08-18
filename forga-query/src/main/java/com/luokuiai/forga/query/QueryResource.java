package com.luokuiai.forga.query;

/**
 * Caller-defined resource shape used by typed query constraints.
 *
 * @param type resource type
 */
public record QueryResource(String type) {

  /**
   * Creates a query resource.
   *
   * @param type resource type
   */
  public QueryResource {
    type = QueryValidator.name("resource type", type);
  }
}
