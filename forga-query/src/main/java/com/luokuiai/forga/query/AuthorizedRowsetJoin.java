package com.luokuiai.forga.query;

import java.util.List;
import java.util.Objects;

/**
 * Set-based join from a business resource to an authorization rowset.
 *
 * @param resource business resource mapping
 * @param rowset authorization rowset mapping
 * @param correlations join correlations between resource and rowset fields
 */
public record AuthorizedRowsetJoin(
    ResourceQueryMapping resource,
    ResourceQueryMapping rowset,
    List<QueryCorrelation> correlations) {

  /**
   * Creates an authorized rowset join.
   *
   * @param resource business resource mapping
   * @param rowset authorization rowset mapping
   * @param correlations join correlations
   */
  public AuthorizedRowsetJoin {
    resource = Objects.requireNonNull(resource, "resource is required");
    rowset = Objects.requireNonNull(rowset, "rowset is required");
    correlations = List.copyOf(Objects.requireNonNull(correlations, "correlations are required"));
    if (correlations.isEmpty()) {
      throw new IllegalArgumentException("correlations must not be empty");
    }
    for (QueryCorrelation correlation : correlations) {
      if (!resource.resource().equals(correlation.outer().resource())) {
        throw new IllegalArgumentException("outer correlation must use resource mapping");
      }
      if (!rowset.resource().equals(correlation.inner().resource())) {
        throw new IllegalArgumentException("inner correlation must use rowset mapping");
      }
    }
  }
}
