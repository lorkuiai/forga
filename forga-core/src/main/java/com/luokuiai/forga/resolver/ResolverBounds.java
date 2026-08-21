package com.luokuiai.forga.resolver;

final class ResolverBounds {

  static final int MAX_LIMIT = 1000;

  static final int MAX_BATCH_SIZE = 500;

  private ResolverBounds() {
  }

  static int limit(int value) {
    if (value < 1 || value > MAX_LIMIT) {
      throw new IllegalArgumentException("limit must be between 1 and " + MAX_LIMIT);
    }
    return value;
  }

  static int batchSize(int value) {
    if (value < 1 || value > MAX_BATCH_SIZE) {
      throw new IllegalArgumentException("batch size must be between 1 and " + MAX_BATCH_SIZE);
    }
    return value;
  }
}
