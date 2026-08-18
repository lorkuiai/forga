package com.luokuiai.forga.mybatis;

import com.luokuiai.forga.core.model.AttributeRef;
import java.util.Map;

/**
 * Supplies neutral request attributes for authorization.
 */
@FunctionalInterface
public interface ForgaRequestAttributesProvider {

  /**
   * Returns request attributes.
   *
   * @return immutable or mutable attribute map
   */
  Map<AttributeRef, String> attributes();
}
