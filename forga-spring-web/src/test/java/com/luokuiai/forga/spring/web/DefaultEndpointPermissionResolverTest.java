package com.luokuiai.forga.spring.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import com.luokuiai.forga.core.model.PermissionRef;
import jakarta.annotation.security.PermitAll;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.method.HandlerMethod;

class DefaultEndpointPermissionResolverTest {

  private final DefaultEndpointPermissionResolver resolver =
      new DefaultEndpointPermissionResolver();

  @Test
  void resolvesMethodPermissionBeforeTypeMetadata() throws Exception {
    EndpointPermissionRequirement requirement = resolve("maintain");

    assertThat(requirement.permission())
        .contains(new PermissionRef("meeting:maintain"));
  }

  @Test
  void resolvesTypePermission() throws Exception {
    EndpointPermissionRequirement requirement = resolve("inherited");

    assertThat(requirement.permission()).contains(new PermissionRef("meeting:view"));
  }

  @Test
  void resolvesMethodPermitAllBeforeTypePermission() throws Exception {
    assertThat(resolve("health").isPermitAll()).isTrue();
  }

  @Test
  void leavesUnannotatedHandlerUnresolved() throws Exception {
    HandlerMethod handler = new HandlerMethod(new PlainController(), "plain");

    assertThat(resolver.resolve(handler, new MockHttpServletRequest())).isEmpty();
  }

  @Test
  void rejectsConflictingMethodMetadata() throws Exception {
    HandlerMethod handler = new HandlerMethod(new DemoController(), "conflicting");

    assertThatIllegalStateException()
        .isThrownBy(() -> resolver.resolve(handler, new MockHttpServletRequest()))
        .withMessageContaining("both");
  }

  private EndpointPermissionRequirement resolve(String method) throws Exception {
    HandlerMethod handler = new HandlerMethod(new DemoController(), method);
    return resolver.resolve(handler, new MockHttpServletRequest()).orElseThrow();
  }

  @RequiresPermission("meeting:view")
  private static final class DemoController {

    @RequiresPermission("meeting:maintain")
    public void maintain() {
    }

    public void inherited() {
    }

    @PermitAll
    public void health() {
    }

    @PermitAll
    @RequiresPermission("meeting:view")
    public void conflicting() {
    }
  }

  private static final class PlainController {

    public void plain() {
    }
  }
}
