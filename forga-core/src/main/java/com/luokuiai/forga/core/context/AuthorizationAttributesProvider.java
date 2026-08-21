package com.luokuiai.forga.core.context;

import com.luokuiai.forga.core.model.AttributeRef;
import java.util.Map;

/** Supplies neutral attributes for the current authorization invocation. */
@FunctionalInterface
public interface AuthorizationAttributesProvider {

  /**
   * Returns current authorization attributes.
   *
   * @return immutable or mutable attribute map
   */
  Map<AttributeRef, String> attributes();
}
