package com.luokuiai.forga.core.eval;

import java.util.List;
import java.util.Map;

/**
 * Batch-capable reverse relationship lookup used by object listing.
 */
@FunctionalInterface
public interface ObjectListingLookup {

  /**
   * Resolves reverse relationship requests.
   *
   * @param requests reverse lookup requests
   * @return pages keyed by request
   */
  Map<ReverseRelationLookupRequest, ObjectListingPage> resolve(
      List<ReverseRelationLookupRequest> requests);
}
