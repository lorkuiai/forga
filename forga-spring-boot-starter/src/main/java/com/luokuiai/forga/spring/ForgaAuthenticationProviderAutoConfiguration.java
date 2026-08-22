package com.luokuiai.forga.spring;

import com.luokuiai.forga.core.context.AuthenticatedSubjectProvider;
import java.util.List;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/** Validates that enabled Forga integration has one unambiguous authentication provider. */
@AutoConfiguration
@ConditionalOnForgaEnabled
public class ForgaAuthenticationProviderAutoConfiguration {

  /**
   * Validates authenticated-subject provider discovery after singleton assembly.
   *
   * @param providers discovered authenticated-subject providers
   * @return startup validation callback
   */
  @Bean
  public SmartInitializingSingleton forgaAuthenticationProviderValidation(
      ObjectProvider<AuthenticatedSubjectProvider> providers) {
    return () -> validate(providers.orderedStream().toList());
  }

  private static void validate(List<AuthenticatedSubjectProvider> providers) {
    if (providers.isEmpty()) {
      throw new ForgaRuntimeException("enabled integration requires an authentication provider");
    }
    if (providers.size() > 1) {
      throw new ForgaRuntimeException(
          "enabled integration requires exactly one authentication provider, found "
              + providers.size());
    }
  }
}
