package com.luokuiai.forga.spring.web;

import com.luokuiai.forga.core.model.PermissionRef;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;

/** Resolves Forga permission and Jakarta permit-all annotations. */
public final class DefaultEndpointPermissionResolver implements EndpointPermissionResolver {

  @Override
  public Optional<EndpointPermissionRequirement> resolve(
      HandlerMethod handlerMethod, HttpServletRequest request) {
    Optional<EndpointPermissionRequirement> method = resolveElement(handlerMethod.getMethod());
    if (method.isPresent()) {
      return method;
    }
    return resolveElement(handlerMethod.getBeanType());
  }

  private static Optional<EndpointPermissionRequirement> resolveElement(AnnotatedElement element) {
    RequiresPermission required =
        AnnotatedElementUtils.findMergedAnnotation(element, RequiresPermission.class);
    PermitAll permitAll = AnnotatedElementUtils.findMergedAnnotation(element, PermitAll.class);
    if (required != null && permitAll != null) {
      throw new IllegalStateException("endpoint declares both required permission and permit-all");
    }
    if (required != null) {
      return Optional.of(
          EndpointPermissionRequirement.required(new PermissionRef(required.value())));
    }
    if (permitAll != null) {
      return Optional.of(EndpointPermissionRequirement.permitAll());
    }
    return Optional.empty();
  }
}
