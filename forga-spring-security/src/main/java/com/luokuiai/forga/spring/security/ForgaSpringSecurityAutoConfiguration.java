package com.luokuiai.forga.spring.security;

import com.luokuiai.forga.core.context.AuthenticatedSubjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;

/** Spring Boot assembly for Spring Security authenticated-subject mapping. */
@AutoConfiguration
@ConditionalOnClass(SecurityContextHolder.class)
public class ForgaSpringSecurityAutoConfiguration {

  /**
   * Creates the Spring Security subject provider for the current security context.
   *
   * @return authenticated-subject provider
   */
  @Bean
  public AuthenticatedSubjectProvider forgaSpringSecuritySubjectProvider() {
    return new SpringSecurityAuthenticatedSubjectProvider();
  }
}
