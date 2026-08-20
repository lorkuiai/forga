package com.luokuiai.forga.spring.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Spring MVC interceptor that enforces {@link RequiresResource} on handler methods.
 */
public final class RequiresResourceInterceptor implements HandlerInterceptor {

  private final ResourceAuthorizationService resourceAuthorizationService;

  /**
   * Creates a resource interceptor.
   *
   * @param resourceAuthorizationService authorization service
   */
  public RequiresResourceInterceptor(ResourceAuthorizationService resourceAuthorizationService) {
    this.resourceAuthorizationService = resourceAuthorizationService;
  }

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    if (!(handler instanceof HandlerMethod handlerMethod)) {
      return true;
    }
    RequiresResource annotation = handlerMethod.getMethodAnnotation(RequiresResource.class);
    if (annotation == null || RequiresResource.NONE.equals(annotation.value())) {
      return true;
    }
    resourceAuthorizationService.requireResource(
        new ResourceInvocation(
            annotation.value(),
            Optional.of(handlerMethod.getMethod()),
            Optional.of(handlerMethod.getBean()),
            Optional.of(request)));
    return true;
  }
}
