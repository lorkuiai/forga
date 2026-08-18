package com.luokuiai.forga.core.eval;

import java.util.List;
import java.util.Map;

/**
 * Batch relationship lookup used by the evaluator.
 */
public interface RelationshipLookup {

  /**
   * Resolves a bounded batch of relation lookup requests.
   *
   * @param requests immutable lookup requests
   * @return entries keyed by request
   */
  Map<RelationLookupRequest, List<RelationshipEntry>> resolve(
      List<RelationLookupRequest> requests);
}
