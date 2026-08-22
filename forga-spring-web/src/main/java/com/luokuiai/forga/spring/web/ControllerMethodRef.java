package com.luokuiai.forga.spring.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/** Exact reference to a controller method that may be registered externally. */
public record ControllerMethodRef(
    Class<?> controllerType, String methodName, List<Class<?>> parameterTypes) {

  /**
   * Creates an exact controller method reference.
   *
   * @param controllerType controller class
   * @param methodName method name
   * @param parameterTypes exact method parameter types
   */
  public ControllerMethodRef {
    controllerType = Objects.requireNonNull(controllerType, "controller type is required");
    if (methodName == null || methodName.isBlank()) {
      throw new IllegalArgumentException("method name is required");
    }
    methodName = methodName.trim();
    Objects.requireNonNull(parameterTypes, "parameter types are required");
    List<Class<?>> checkedParameterTypes = new ArrayList<>();
    for (Class<?> parameterType : parameterTypes) {
      checkedParameterTypes.add(
          Objects.requireNonNull(parameterType, "parameter type is required"));
    }
    parameterTypes = List.copyOf(checkedParameterTypes);
  }

  /**
   * Creates an exact controller method reference.
   *
   * @param controllerType controller class
   * @param methodName method name
   * @param parameterTypes exact method parameter types
   * @return controller method reference
   */
  public static ControllerMethodRef of(
      Class<?> controllerType, String methodName, Class<?>... parameterTypes) {
    Objects.requireNonNull(parameterTypes, "parameter types are required");
    return new ControllerMethodRef(
        controllerType, methodName, Arrays.asList(parameterTypes.clone()));
  }

  /**
   * Returns a stable readable signature for diagnostics.
   *
   * @return controller method signature
   */
  public String signature() {
    String parameters =
        parameterTypes.stream().map(Class::getTypeName).collect(Collectors.joining(", "));
    return controllerType.getName() + "#" + methodName + "(" + parameters + ")";
  }
}
