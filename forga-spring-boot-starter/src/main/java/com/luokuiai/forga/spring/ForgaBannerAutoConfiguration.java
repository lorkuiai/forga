package com.luokuiai.forga.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/** Logs the Forga banner after enabled integration has been assembled. */
@AutoConfiguration(
    after = {
      ForgaAuthenticationProviderAutoConfiguration.class,
      ForgaEvaluatorAutoConfiguration.class,
      ForgaMyBatisAutoConfiguration.class,
      ForgaPermissionCatalogAutoConfiguration.class,
      ForgaSpringWebAutoConfiguration.class
    })
@ConditionalOnForgaEnabled
public class ForgaBannerAutoConfiguration {

  private static final Log LOGGER = LogFactory.getLog(ForgaBannerAutoConfiguration.class);

  /**
   * Logs the Forga startup banner after singleton assembly.
   *
   * @return startup banner callback
   */
  @Bean
  public SmartInitializingSingleton forgaStartupBanner() {
    return () -> LOGGER.info(ForgaStartupBanner.render());
  }
}
