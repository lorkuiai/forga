package com.luokuiai.forga.spring.web;

import com.luokuiai.forga.core.catalog.PermissionDefinition;
import com.luokuiai.forga.core.model.PermissionRef;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.web.method.HandlerMethod;

/** Immutable external endpoint permission registrations assembled from host contributors. */
public final class EndpointPermissionRegistrations {

  private final Map<ControllerMethodRef, EndpointPermissionRequirement> requirements;

  private final List<PermissionDefinition> definitions;

  EndpointPermissionRegistrations(
      Map<ControllerMethodRef, EndpointPermissionRequirement> requirements,
      Map<PermissionRef, PermissionDefinition> definitions) {
    this.requirements = Map.copyOf(requirements);
    this.definitions =
        definitions.values().stream()
            .sorted(Comparator.comparing(definition -> definition.permission().name()))
            .toList();
  }

  /**
   * Assembles immutable registrations from independent contributors.
   *
   * @param contributors endpoint permission contributors
   * @return immutable registrations
   */
  public static EndpointPermissionRegistrations fromContributors(
      Collection<? extends EndpointPermissionContributor> contributors) {
    Objects.requireNonNull(contributors, "contributors are required");
    EndpointPermissionRegistry registry = new EndpointPermissionRegistry();
    for (EndpointPermissionContributor contributor : contributors) {
      Objects.requireNonNull(contributor, "contributor is required").contribute(registry);
    }
    return registry.snapshot();
  }

  /**
   * Returns unique permission definitions contributed by required endpoints.
   *
   * @return immutable permission definitions
   */
  public List<PermissionDefinition> definitions() {
    return definitions;
  }

  /**
   * Compiles registrations against actual Spring MVC handlers and composes host resolvers.
   *
   * @param handlers discovered Spring MVC handlers
   * @param hostResolvers additional host endpoint resolvers
   * @return compiled endpoint permission resolver
   */
  public EndpointPermissionResolver compile(
      Collection<HandlerMethod> handlers,
      Collection<? extends EndpointPermissionResolver> hostResolvers) {
    Objects.requireNonNull(handlers, "handlers are required");
    Objects.requireNonNull(hostResolvers, "host resolvers are required");
    Map<HandlerKey, EndpointPermissionRequirement> compiled = new LinkedHashMap<>();
    for (Map.Entry<ControllerMethodRef, EndpointPermissionRequirement> entry :
        requirements.entrySet()) {
      List<HandlerMethod> matches = matchingHandlers(entry.getKey(), handlers);
      if (matches.isEmpty()) {
        throw new IllegalStateException(
            "registered endpoint is not a Spring MVC handler: " + entry.getKey().signature());
      }
      for (HandlerMethod handler : matches) {
        Optional<EndpointPermissionRequirement> annotated =
            DefaultEndpointPermissionResolver.resolve(handler);
        if (annotated.isPresent() && !annotated.orElseThrow().equals(entry.getValue())) {
          throw new IllegalStateException(
              "conflicting annotation and registered permission: " + entry.getKey().signature());
        }
        compiled.put(HandlerKey.of(handler), entry.getValue());
      }
    }
    List<EndpointPermissionResolver> checkedHostResolvers = new ArrayList<>();
    for (EndpointPermissionResolver resolver : hostResolvers) {
      checkedHostResolvers.add(Objects.requireNonNull(resolver, "host resolver is required"));
    }
    return new CompiledEndpointPermissionResolver(compiled, checkedHostResolvers);
  }

  private static List<HandlerMethod> matchingHandlers(
      ControllerMethodRef endpoint, Collection<HandlerMethod> handlers) {
    return handlers.stream()
        .filter(handler -> endpoint.controllerType().equals(handler.getBeanType()))
        .filter(handler -> handler.getMethod().getName().equals(endpoint.methodName()))
        .filter(
            handler ->
                List.of(handler.getMethod().getParameterTypes()).equals(endpoint.parameterTypes()))
        .toList();
  }

  private record HandlerKey(Class<?> beanType, Method method) {

    private static HandlerKey of(HandlerMethod handler) {
      return new HandlerKey(handler.getBeanType(), handler.getMethod());
    }
  }

  private static final class CompiledEndpointPermissionResolver
      implements EndpointPermissionResolver {

    private final DefaultEndpointPermissionResolver annotations =
        new DefaultEndpointPermissionResolver();

    private final Map<HandlerKey, EndpointPermissionRequirement> registrations;

    private final List<EndpointPermissionResolver> hostResolvers;

    private CompiledEndpointPermissionResolver(
        Map<HandlerKey, EndpointPermissionRequirement> registrations,
        List<EndpointPermissionResolver> hostResolvers) {
      this.registrations = Map.copyOf(registrations);
      this.hostResolvers = List.copyOf(hostResolvers);
    }

    @Override
    public Optional<EndpointPermissionRequirement> resolve(
        HandlerMethod handlerMethod, HttpServletRequest request) {
      List<EndpointPermissionRequirement> resolved = new ArrayList<>();
      annotations.resolve(handlerMethod, request).ifPresent(resolved::add);
      Optional.ofNullable(registrations.get(HandlerKey.of(handlerMethod))).ifPresent(resolved::add);
      for (EndpointPermissionResolver resolver : hostResolvers) {
        resolver.resolve(handlerMethod, request).ifPresent(resolved::add);
      }
      if (resolved.isEmpty()) {
        return Optional.empty();
      }
      EndpointPermissionRequirement requirement = resolved.get(0);
      if (resolved.stream().anyMatch(candidate -> !candidate.equals(requirement))) {
        throw EndpointAuthorizationException.conflictingMetadata();
      }
      return Optional.of(requirement);
    }
  }
}
