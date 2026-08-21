package com.luokuiai.forga.resolver;

import com.luokuiai.forga.core.model.ConsistencyToken;

/**
 * Opaque cursor returned by paginated resolver operations.
 *
 * @param value cursor value
 */
public record PageCursor(String value) {

  /**
   * Creates a page cursor.
   *
   * @param value cursor value
   */
  public PageCursor {
    value = new ConsistencyToken(value).value();
  }
}
