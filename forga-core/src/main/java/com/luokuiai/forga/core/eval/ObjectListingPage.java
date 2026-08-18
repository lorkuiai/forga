package com.luokuiai.forga.core.eval;

import com.luokuiai.forga.core.model.ConsistencyToken;
import com.luokuiai.forga.core.model.ObjectRef;
import java.util.List;
import java.util.Optional;

/**
 * Page returned by a reverse object listing lookup.
 *
 * @param objects resolved objects
 * @param nextCursor optional continuation cursor
 * @param consistency optional consistency token returned by the resolver
 */
public record ObjectListingPage(
    List<ObjectRef> objects,
    Optional<ListObjectsCursor> nextCursor,
    Optional<ConsistencyToken> consistency) {

  /**
   * Creates a listing page without a continuation cursor.
   *
   * @param objects resolved objects
   */
  public ObjectListingPage(List<ObjectRef> objects) {
    this(objects, Optional.empty(), Optional.empty());
  }

  /**
   * Creates a listing page.
   *
   * @param objects resolved objects
   * @param nextCursor optional continuation cursor
   */
  public ObjectListingPage(List<ObjectRef> objects, Optional<ListObjectsCursor> nextCursor) {
    this(objects, nextCursor, Optional.empty());
  }

  /**
   * Creates a listing page.
   *
   * @param objects resolved objects
   * @param nextCursor optional continuation cursor
   * @param consistency optional consistency token returned by the resolver
   */
  public ObjectListingPage {
    objects = List.copyOf(objects);
    nextCursor = nextCursor == null ? Optional.empty() : nextCursor;
    consistency = consistency == null ? Optional.empty() : consistency;
  }
}
