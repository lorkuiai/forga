package com.luokuiai.forga.spring;

import com.luokuiai.forga.core.catalog.PermissionCatalog;
import com.luokuiai.forga.core.catalog.PermissionCatalogContributor;
import com.luokuiai.forga.core.catalog.PermissionCatalogSynchronizer;
import java.util.List;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/** Spring Boot assembly and host synchronization for the permission catalog. */
@AutoConfiguration
@ConditionalOnProperty(prefix = "forga", name = "enabled", havingValue = "true")
public class ForgaPermissionCatalogAutoConfiguration {

  /**
   * Assembles permission definitions from independent host modules.
   *
   * @param contributors host permission contributors
   * @return immutable permission catalog
   */
  @Bean
  @ConditionalOnMissingBean
  public PermissionCatalog forgaPermissionCatalog(
      List<PermissionCatalogContributor> contributors) {
    return PermissionCatalog.fromContributors(contributors);
  }

  /**
   * Synchronizes the assembled catalog when host persistence is configured.
   *
   * @param catalog assembled permission catalog
   * @param synchronizer host synchronizer provider
   * @return startup synchronization callback
   */
  @Bean
  public SmartInitializingSingleton forgaPermissionCatalogSynchronization(
      PermissionCatalog catalog,
      ObjectProvider<PermissionCatalogSynchronizer> synchronizer) {
    return () -> synchronizer.ifAvailable(candidate -> candidate.synchronize(catalog));
  }
}
