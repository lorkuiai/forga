package com.luokuiai.forga.spring;

import com.luokuiai.forga.core.catalog.PermissionCatalogContributor;
import com.luokuiai.forga.spring.web.EndpointPermissionAuthorizer;
import com.luokuiai.forga.spring.web.EndpointPermissionContributor;
import com.luokuiai.forga.spring.web.EndpointPermissionInterceptor;
import com.luokuiai.forga.spring.web.EndpointPermissionRegistrations;
import com.luokuiai.forga.spring.web.EndpointPermissionRequirement;
import com.luokuiai.forga.spring.web.EndpointPermissionResolver;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/** Spring Boot assembly for externally registered Spring MVC endpoint permissions. */
@AutoConfiguration
@AutoConfigureBefore(ForgaPermissionCatalogAutoConfiguration.class)
@ConditionalOnClass(WebMvcConfigurer.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnForgaEnabled
@ConditionalOnBean(EndpointPermissionContributor.class)
public class ForgaSpringWebAutoConfiguration {

  /**
   * Assembles immutable endpoint registrations from host contributors.
   *
   * @param contributors endpoint permission contributors
   * @return immutable endpoint registrations
   */
  @Bean
  @ConditionalOnMissingBean
  public EndpointPermissionRegistrations forgaEndpointPermissionRegistrations(
      List<EndpointPermissionContributor> contributors) {
    return EndpointPermissionRegistrations.fromContributors(contributors);
  }

  /**
   * Adapts endpoint permission definitions into the ordinary permission catalog.
   *
   * @param registrations assembled endpoint registrations
   * @return permission catalog contributor
   */
  @Bean("forgaEndpointPermissionCatalogContributor")
  public PermissionCatalogContributor forgaEndpointPermissionCatalogContributor(
      EndpointPermissionRegistrations registrations) {
    return registrations::definitions;
  }

  @Bean
  EndpointPermissionResolverDelegate forgaEndpointPermissionResolverDelegate() {
    return new EndpointPermissionResolverDelegate();
  }

  @Bean
  SmartInitializingSingleton forgaEndpointPermissionResolverCompilation(
      EndpointPermissionRegistrations registrations,
      ObjectProvider<EndpointPermissionResolver> hostResolvers,
      @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping,
      EndpointPermissionResolverDelegate delegate) {
    return () ->
        delegate.initialize(
            registrations.compile(
                handlerMapping.getHandlerMethods().values(),
                hostResolvers.orderedStream().toList()));
  }

  @Bean
  SmartInitializingSingleton forgaEndpointPermissionEnforcementValidation(
      ObjectProvider<EndpointPermissionAuthorizer> authorizer,
      ObjectProvider<EndpointPermissionInterceptor> interceptor) {
    return () -> {
      if (authorizer.getIfAvailable() == null && interceptor.getIfAvailable() == null) {
        throw new IllegalStateException(
            "endpoint permission authorizer or host interceptor is required");
      }
    };
  }

  /** Auto-configures endpoint enforcement when the host supplies an authorizer. */
  @Configuration(proxyBeanMethods = false)
  @ConditionalOnBean(EndpointPermissionAuthorizer.class)
  @ConditionalOnMissingBean(EndpointPermissionInterceptor.class)
  static class EndpointEnforcementConfiguration {

    @Bean
    EndpointPermissionInterceptor forgaEndpointPermissionInterceptor(
        EndpointPermissionAuthorizer authorizer, EndpointPermissionResolverDelegate delegate) {
      return new EndpointPermissionInterceptor(delegate::resolve, authorizer);
    }

    @Bean("forgaEndpointPermissionWebMvcConfigurer")
    WebMvcConfigurer forgaEndpointPermissionWebMvcConfigurer(
        EndpointPermissionInterceptor interceptor) {
      return new WebMvcConfigurer() {
        @Override
        public void addInterceptors(InterceptorRegistry registry) {
          registry.addInterceptor(interceptor);
        }
      };
    }
  }

  static final class EndpointPermissionResolverDelegate {

    private EndpointPermissionResolver delegate;

    void initialize(EndpointPermissionResolver resolver) {
      if (delegate != null) {
        throw new IllegalStateException("endpoint permission resolver is already initialized");
      }
      delegate = Objects.requireNonNull(resolver, "resolver is required");
    }

    Optional<EndpointPermissionRequirement> resolve(
        HandlerMethod handlerMethod, HttpServletRequest request) {
      if (delegate == null) {
        throw new IllegalStateException("endpoint permission resolver is not initialized");
      }
      return delegate.resolve(handlerMethod, request);
    }
  }
}
