package com.luokuiai.forga.core.catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import com.luokuiai.forga.core.model.PermissionRef;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class PermissionCatalogTest {

  private static final PermissionDefinition VIEW =
      new PermissionDefinition(new PermissionRef("meeting:view"), "View meeting", "meeting");

  private static final PermissionDefinition MAINTAIN =
      new PermissionDefinition(
          new PermissionRef("meeting:maintain"), "Maintain meeting", "meeting");

  @Test
  void assemblesDeterministicCatalogFromContributors() {
    PermissionCatalog catalog =
        PermissionCatalog.fromContributors(List.of(() -> List.of(VIEW), () -> List.of(MAINTAIN)));

    assertThat(catalog.definitions()).containsExactly(MAINTAIN, VIEW);
    assertThat(catalog.find(VIEW.permission())).contains(VIEW);
  }

  @Test
  void rejectsDuplicatePermissions() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> PermissionCatalog.fromContributors(List.of(() -> List.of(VIEW, VIEW))))
        .withMessageContaining("meeting:view");
  }

  @Test
  void rejectsInvalidDefinitions() {
    assertThatNullPointerException()
        .isThrownBy(() -> new PermissionDefinition(null, "View meeting", "meeting"));
    assertThatIllegalArgumentException()
        .isThrownBy(
            () -> new PermissionDefinition(new PermissionRef("meeting:view"), " ", "meeting"));
  }

  @Test
  void handsImmutableCatalogToHostSynchronizer() {
    PermissionCatalog catalog = new PermissionCatalog(List.of(VIEW));
    AtomicReference<PermissionCatalog> synchronizedCatalog = new AtomicReference<>();
    PermissionCatalogSynchronizer synchronizer = synchronizedCatalog::set;

    synchronizer.synchronize(catalog);

    assertThat(synchronizedCatalog).hasValue(catalog);
    assertThat(catalog.definitions()).isUnmodifiable();
  }
}
