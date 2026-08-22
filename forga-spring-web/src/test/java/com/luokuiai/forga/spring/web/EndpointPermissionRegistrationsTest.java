package com.luokuiai.forga.spring.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.luokuiai.forga.core.catalog.PermissionDefinition;
import com.luokuiai.forga.core.model.PermissionRef;
import jakarta.annotation.security.PermitAll;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.method.HandlerMethod;

class EndpointPermissionRegistrationsTest {

  private static final PermissionDefinition VIEW =
      new PermissionDefinition(
          new PermissionRef("vendor:order:view"), "View vendor order", "vendor-sdk");

  @Test
  void resolvesOnlyExactRegisteredOverload() throws Exception {
    EndpointPermissionRegistrations registrations =
        registrations(
            registry -> registry.require(DemoController.class, "order", VIEW, String.class));
    HandlerMethod stringHandler = handler("order", String.class);
    HandlerMethod longHandler = handler("order", long.class);
    EndpointPermissionResolver resolver =
        registrations.compile(List.of(stringHandler, longHandler), List.of());

    assertThat(resolver.resolve(stringHandler, request()).orElseThrow().permission())
        .contains(VIEW.permission());
    assertThat(resolver.resolve(longHandler, request())).isEmpty();
  }

  @Test
  void resolvesRegisteredPermitAll() throws Exception {
    EndpointPermissionRegistrations registrations =
        registrations(registry -> registry.permitAll(DemoController.class, "health"));
    HandlerMethod handler = handler("health");

    EndpointPermissionRequirement requirement =
        registrations
            .compile(List.of(handler), List.of())
            .resolve(handler, request())
            .orElseThrow();

    assertThat(requirement.isPermitAll()).isTrue();
    assertThat(registrations.definitions()).isEmpty();
  }

  @Test
  void deduplicatesIdenticalDefinitionsAcrossEndpoints() {
    EndpointPermissionRegistrations registrations =
        registrations(
            registry -> {
              registry.require(DemoController.class, "order", VIEW, String.class);
              registry.require(DemoController.class, "annotated", VIEW);
            });

    assertThat(registrations.definitions()).containsExactly(VIEW);
  }

  @Test
  void rejectsConflictingDefinitionsAndEndpointRequirements() {
    PermissionDefinition conflicting =
        new PermissionDefinition(VIEW.permission(), "Different name", "vendor-sdk");

    assertThatIllegalArgumentException()
        .isThrownBy(
            () ->
                registrations(
                    registry -> {
                      registry.require(DemoController.class, "order", VIEW, String.class);
                      registry.require(DemoController.class, "annotated", conflicting);
                    }))
        .withMessageContaining("conflicting permission definition");
    assertThatIllegalArgumentException()
        .isThrownBy(
            () ->
                registrations(
                    registry -> {
                      registry.require(DemoController.class, "health", VIEW);
                      registry.permitAll(DemoController.class, "health");
                    }))
        .withMessageContaining("conflicting endpoint permission");
  }

  @Test
  void rejectsMissingHandlerAndAnnotationConflict() throws Exception {
    EndpointPermissionRegistrations missing =
        registrations(registry -> registry.require(DemoController.class, "missing", VIEW));
    EndpointPermissionRegistrations conflict =
        registrations(registry -> registry.require(DemoController.class, "health", VIEW));

    assertThatIllegalStateException()
        .isThrownBy(() -> missing.compile(List.of(handler("health")), List.of()))
        .withMessageContaining("not a Spring MVC handler")
        .withMessageContaining("#missing");
    assertThatIllegalStateException()
        .isThrownBy(() -> conflict.compile(List.of(handler("health")), List.of()))
        .withMessageContaining("conflicting annotation");
  }

  @Test
  void acceptsMatchingAnnotationAndFailsClosedForDynamicConflict() throws Exception {
    EndpointPermissionRegistrations registrations =
        registrations(registry -> registry.require(DemoController.class, "annotated", VIEW));
    HandlerMethod handler = handler("annotated");
    EndpointPermissionResolver resolver =
        registrations.compile(
            List.of(handler),
            List.of(
                (ignoredHandler, ignoredRequest) ->
                    Optional.of(EndpointPermissionRequirement.permitAll())));

    assertThatThrownBy(() -> resolver.resolve(handler, request()))
        .isInstanceOf(EndpointAuthorizationException.class)
        .extracting(exception -> ((EndpointAuthorizationException) exception).reason())
        .isEqualTo("ENDPOINT_PERMISSION_CONFLICT");
  }

  @Test
  void validatesControllerMethodReferenceInputs() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> ControllerMethodRef.of(DemoController.class, " "))
        .withMessageContaining("method name");
    assertThatThrownBy(
            () ->
                new ControllerMethodRef(
                    DemoController.class, "order", java.util.Arrays.asList((Class<?>) null)))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("parameter type");
  }

  private static EndpointPermissionRegistrations registrations(
      EndpointPermissionContributor contributor) {
    return EndpointPermissionRegistrations.fromContributors(List.of(contributor));
  }

  private static HandlerMethod handler(String method, Class<?>... parameterTypes) throws Exception {
    return new HandlerMethod(new DemoController(), method, parameterTypes);
  }

  private static MockHttpServletRequest request() {
    return new MockHttpServletRequest();
  }

  private static final class DemoController {

    public void order(String orderId) {
    }

    public void order(long orderId) {
    }

    @RequiresPermission("vendor:order:view")
    public void annotated() {
    }

    @PermitAll
    public void health() {
    }
  }
}
