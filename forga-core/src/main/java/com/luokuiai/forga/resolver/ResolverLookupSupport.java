package com.luokuiai.forga.resolver;

import com.luokuiai.forga.core.eval.DecisionReason;
import com.luokuiai.forga.core.eval.RelationshipLookupException;
import java.util.List;

final class ResolverLookupSupport {

  private ResolverLookupSupport() {
  }

  static <T> List<List<T>> batches(List<T> values) {
    if (values.isEmpty()) {
      return List.of();
    }
    return java.util.stream.IntStream.iterate(
            0, start -> start < values.size(), start -> start + ResolverBounds.MAX_BATCH_SIZE)
        .mapToObj(
            start ->
                values.subList(
                    start, Math.min(start + ResolverBounds.MAX_BATCH_SIZE, values.size())))
        .toList();
  }

  static RelationshipLookupException failure(String message) {
    return new RelationshipLookupException(DecisionReason.RESOLVER_FAILURE, message);
  }
}
