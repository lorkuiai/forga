package com.luokuiai.forga.spring.web;

import com.luokuiai.forga.core.catalog.PermissionDefinition;
import com.luokuiai.forga.core.model.PermissionRef;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/** Mutable registration surface supplied to endpoint permission contributors during assembly. */
public final class EndpointPermissionRegistry {

  private final Map<ControllerMethodRef, EndpointPermissionRequirement> requirements =
      new LinkedHashMap<>();

  private final Map<PermissionRef, PermissionDefinition> definitions = new LinkedHashMap<>();

  /**
   * Registers an exact controller method that requires a permission.
   *
   * @param endpoint controller method reference
   * @param definition permission definition added to the ordinary catalog
   * @return this registry
   */
  public EndpointPermissionRegistry require(
      ControllerMethodRef endpoint, PermissionDefinition definition) {
    ControllerMethodRef checkedEndpoint = Objects.requireNonNull(endpoint, "endpoint is required");
    PermissionDefinition checkedDefinition =
        Objects.requireNonNull(definition, "permission definition is required");
    registerDefinition(checkedDefinition);
    register(
        checkedEndpoint, EndpointPermissionRequirement.required(checkedDefinition.permission()));
    return this;
  }

  /**
   * Registers an exact controller method that requires a permission.
   *
   * @param controllerType controller class
   * @param methodName method name
   * @param definition permission definition added to the ordinary catalog
   * @param parameterTypes exact method parameter types
   * @return this registry
   */
  public EndpointPermissionRegistry require(
      Class<?> controllerType,
      String methodName,
      PermissionDefinition definition,
      Class<?>... parameterTypes) {
    return require(ControllerMethodRef.of(controllerType, methodName, parameterTypes), definition);
  }

  /**
   * Registers an exact controller method as public.
   *
   * @param endpoint controller method reference
   * @return this registry
   */
  public EndpointPermissionRegistry permitAll(ControllerMethodRef endpoint) {
    register(
        Objects.requireNonNull(endpoint, "endpoint is required"),
        EndpointPermissionRequirement.permitAll());
    return this;
  }

  /**
   * Registers an exact controller method as public.
   *
   * @param controllerType controller class
   * @param methodName method name
   * @param parameterTypes exact method parameter types
   * @return this registry
   */
  public EndpointPermissionRegistry permitAll(
      Class<?> controllerType, String methodName, Class<?>... parameterTypes) {
    return permitAll(ControllerMethodRef.of(controllerType, methodName, parameterTypes));
  }

  EndpointPermissionRegistrations snapshot() {
    return new EndpointPermissionRegistrations(requirements, definitions);
  }

  private void register(ControllerMethodRef endpoint, EndpointPermissionRequirement requirement) {
    EndpointPermissionRequirement existing = requirements.putIfAbsent(endpoint, requirement);
    if (existing != null && !existing.equals(requirement)) {
      throw new IllegalArgumentException(
          "conflicting endpoint permission: " + endpoint.signature());
    }
  }

  private void registerDefinition(PermissionDefinition definition) {
    PermissionDefinition existing = definitions.putIfAbsent(definition.permission(), definition);
    if (existing != null && !existing.equals(definition)) {
      throw new IllegalArgumentException(
          "conflicting permission definition: " + definition.permission().name());
    }
  }
}
