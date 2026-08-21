package com.luokuiai.forga.spring.web;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.web.method.HandlerMethod;

/** Resolves host-owned permission metadata for Spring Web endpoints. */
@FunctionalInterface
public interface EndpointPermissionResolver {

  /**
   * Resolves required-permission or permit-all metadata.
   *
   * @param handlerMethod Spring handler method
   * @param request current HTTP request
   * @return resolved metadata, or empty when the endpoint is unresolved
   */
  Optional<EndpointPermissionRequirement> resolve(
      HandlerMethod handlerMethod, HttpServletRequest request);
}
