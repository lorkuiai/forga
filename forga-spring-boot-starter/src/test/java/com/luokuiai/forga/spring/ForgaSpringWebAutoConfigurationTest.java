package com.luokuiai.forga.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.luokuiai.forga.core.catalog.PermissionCatalog;
import com.luokuiai.forga.core.catalog.PermissionDefinition;
import com.luokuiai.forga.core.model.PermissionRef;
import com.luokuiai.forga.spring.web.EndpointAuthorizationDecision;
import com.luokuiai.forga.spring.web.EndpointPermissionAuthorizer;
import com.luokuiai.forga.spring.web.EndpointPermissionContributor;
import com.luokuiai.forga.spring.web.EndpointPermissionInterceptor;
import com.luokuiai.forga.spring.web.EndpointPermissionRegistrations;
import com.luokuiai.forga.spring.web.EndpointPermissionRequirement;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

class ForgaSpringWebAutoConfigurationTest {

  @Test
  void registersSdkPermissionsInCatalogAndEnforcesMappedHandler() {
    runner(ValidWebConfiguration.class)
        .run(
            context -> {
              assertThat(context).hasSingleBean(EndpointPermissionRegistrations.class);
              assertThat(context).hasSingleBean(EndpointPermissionInterceptor.class);
              assertThat(context).hasBean("forgaEndpointPermissionWebMvcConfigurer");
              assertThat(context.getBean(PermissionCatalog.class).definitions())
                  .containsExactly(ValidWebConfiguration.VIEW);

              MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
              assertThatCode(
                      () ->
                          mvc.perform(MockMvcRequestBuilders.get("/vendor/orders/42"))
                              .andExpect(MockMvcResultMatchers.status().isOk()))
                  .doesNotThrowAnyException();
              assertThat(context.getBean(AtomicInteger.class)).hasValue(1);
            });
  }

  @Test
  void doesNotAssembleWebIntegrationWhenForgaIsDisabled() {
    runner(ValidWebConfiguration.class)
        .withPropertyValues("forga.enabled=false")
        .run(
            context -> {
              assertThat(context).doesNotHaveBean(EndpointPermissionRegistrations.class);
              assertThat(context).doesNotHaveBean(EndpointPermissionInterceptor.class);
            });
  }

  @Test
  void backsOffAutoEnforcementForHostInterceptor() {
    runner(CustomInterceptorWebConfiguration.class)
        .run(
            context -> {
              assertThat(context).hasSingleBean(EndpointPermissionInterceptor.class);
              assertThat(context).doesNotHaveBean("forgaEndpointPermissionWebMvcConfigurer");
              assertThat(context.getBean(PermissionCatalog.class).definitions())
                  .containsExactly(ValidWebConfiguration.VIEW);
            });
  }

  @Test
  void failsStartupForRegisteredMethodThatIsNotAHandler() {
    runner(InvalidWebConfiguration.class)
        .run(
            context ->
                assertThat(context.getStartupFailure())
                    .hasMessageContaining("not a Spring MVC handler")
                    .hasMessageContaining("#missing"));
  }

  @Test
  void failsStartupWithoutAuthorizerOrHostInterceptor() {
    runner(MissingAuthorizerWebConfiguration.class)
        .run(
            context ->
                assertThat(context.getStartupFailure())
                    .hasMessageContaining("authorizer or host interceptor is required"));
  }

  private static WebApplicationContextRunner runner(Class<?> configuration) {
    return new WebApplicationContextRunner()
        .withConfiguration(
            AutoConfigurations.of(
                ForgaSpringWebAutoConfiguration.class,
                ForgaPermissionCatalogAutoConfiguration.class))
        .withUserConfiguration(configuration)
        .withPropertyValues("forga.enabled=true");
  }

  @Configuration(proxyBeanMethods = false)
  @EnableWebMvc
  static class ValidWebConfiguration {

    static final PermissionDefinition VIEW =
        new PermissionDefinition(
            new PermissionRef("vendor:order:view"), "View vendor order", "vendor-sdk");

    @Bean
    VendorOrderController vendorOrderController() {
      return new VendorOrderController();
    }

    @Bean
    EndpointPermissionContributor endpointPermissionContributor() {
      return registry ->
          registry.require(VendorOrderController.class, "getOrder", VIEW, String.class);
    }

    @Bean
    AtomicInteger authorizationInvocations() {
      return new AtomicInteger();
    }

    @Bean
    EndpointPermissionAuthorizer endpointPermissionAuthorizer(AtomicInteger invocations) {
      return invocation -> {
        invocations.incrementAndGet();
        return EndpointAuthorizationDecision.allowed(invocation.permission());
      };
    }
  }

  @Configuration(proxyBeanMethods = false)
  @EnableWebMvc
  static class CustomInterceptorWebConfiguration {

    @Bean
    VendorOrderController vendorOrderController() {
      return new VendorOrderController();
    }

    @Bean
    EndpointPermissionContributor endpointPermissionContributor() {
      return registry ->
          registry.require(
              VendorOrderController.class, "getOrder", ValidWebConfiguration.VIEW, String.class);
    }

    @Bean
    EndpointPermissionInterceptor endpointPermissionInterceptor() {
      return new EndpointPermissionInterceptor(
          (handler, request) -> Optional.of(EndpointPermissionRequirement.permitAll()),
          invocation -> EndpointAuthorizationDecision.allowed(invocation.permission()));
    }
  }

  @Configuration(proxyBeanMethods = false)
  @EnableWebMvc
  static class InvalidWebConfiguration {

    @Bean
    VendorOrderController vendorOrderController() {
      return new VendorOrderController();
    }

    @Bean
    EndpointPermissionContributor endpointPermissionContributor() {
      return registry ->
          registry.require(VendorOrderController.class, "missing", ValidWebConfiguration.VIEW);
    }
  }

  @Configuration(proxyBeanMethods = false)
  @EnableWebMvc
  static class MissingAuthorizerWebConfiguration {

    @Bean
    VendorOrderController vendorOrderController() {
      return new VendorOrderController();
    }

    @Bean
    EndpointPermissionContributor endpointPermissionContributor() {
      return registry ->
          registry.require(
              VendorOrderController.class, "getOrder", ValidWebConfiguration.VIEW, String.class);
    }
  }

  @RestController
  static class VendorOrderController {

    @GetMapping("/vendor/orders/{orderId}")
    String getOrder(@PathVariable("orderId") String orderId) {
      return orderId;
    }
  }
}
