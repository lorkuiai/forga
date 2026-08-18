package com.luokuiai.forga.core.eval;

/**
 * Opaque continuation cursor for authorized object listing.
 *
 * @param token stable cursor token
 */
public record ListObjectsCursor(String token) {

  /**
   * Creates a cursor.
   *
   * @param token stable cursor token
   */
  public ListObjectsCursor {
    if (token == null || token.isBlank()) {
      throw new IllegalArgumentException("cursor token is required");
    }
    token = token.trim();
  }
}
