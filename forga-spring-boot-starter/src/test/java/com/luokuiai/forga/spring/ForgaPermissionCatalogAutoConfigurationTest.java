package com.luokuiai.forga.spring;

import static org.assertj.core.api.Assertions.assertThat;

import com.luokuiai.forga.core.catalog.PermissionCatalog;
import com.luokuiai.forga.core.catalog.PermissionCatalogContributor;
import com.luokuiai.forga.core.catalog.PermissionCatalogSynchronizer;
import com.luokuiai.forga.core.catalog.PermissionDefinition;
import com.luokuiai.forga.core.model.PermissionRef;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class ForgaPermissionCatalogAutoConfigurationTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(
              AutoConfigurations.of(ForgaPermissionCatalogAutoConfiguration.class));

  @Test
  void assemblesAndSynchronizesHostPermissionCatalog() {
    AtomicReference<PermissionCatalog> synchronizedCatalog = new AtomicReference<>();
    PermissionDefinition permission =
        new PermissionDefinition(
            new PermissionRef("meeting:view"), "View meeting", "meeting");

    contextRunner
        .withPropertyValues("forga.enabled=true")
        .withBean(PermissionCatalogContributor.class, () -> () -> List.of(permission))
        .withBean(
            PermissionCatalogSynchronizer.class,
            () -> synchronizedCatalog::set)
        .run(
            context -> {
              assertThat(context).hasSingleBean(PermissionCatalog.class);
              assertThat(synchronizedCatalog.get().definitions()).containsExactly(permission);
            });
  }

  @Test
  void assemblesNothingWhenForgaIsDisabled() {
    contextRunner
        .withPropertyValues("forga.enabled=false")
        .run(context -> assertThat(context).doesNotHaveBean(PermissionCatalog.class));
  }
}
