package com.luokuiai.forga.spring.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

class RequiresResourceInterceptorTest {

  @Test
  void checksAnnotatedHandlerMethod() throws Exception {
    List<ResourceInvocation> invocations = new ArrayList<>();
    RequiresResourceInterceptor interceptor =
        new RequiresResourceInterceptor(
            new ResourceAuthorizationService(
                invocation -> {
                  invocations.add(invocation);
                  return ResourceAuthorizationDecision.allowed(invocation.resourceCode());
                }));

    boolean handled =
        interceptor.preHandle(
            new MockHttpServletRequest(),
            new MockHttpServletResponse(),
            new HandlerMethod(new DemoController(), "meeting"));

    assertThat(handled).isTrue();
    assertThat(invocations).hasSize(1);
    ResourceInvocation invocation = invocations.get(0);
    assertThat(invocation.resourceCode()).isEqualTo("rsc:meeting:view");
    assertThat(invocation.method()).isPresent();
    assertThat(invocation.handler()).isPresent();
    assertThat(invocation.request()).isPresent();
  }

  @Test
  void skipsNoneMarker() throws Exception {
    List<ResourceInvocation> invocations = new ArrayList<>();
    RequiresResourceInterceptor interceptor =
        new RequiresResourceInterceptor(
            new ResourceAuthorizationService(
                invocation -> {
                  invocations.add(invocation);
                  return ResourceAuthorizationDecision.allowed(invocation.resourceCode());
                }));

    boolean handled =
        interceptor.preHandle(
            new MockHttpServletRequest(),
            new MockHttpServletResponse(),
            new HandlerMethod(new DemoController(), "health"));

    assertThat(handled).isTrue();
    assertThat(invocations).isEmpty();
  }

  @Test
  void skipsUnannotatedHandlerMethod() throws Exception {
    List<ResourceInvocation> invocations = new ArrayList<>();
    RequiresResourceInterceptor interceptor =
        new RequiresResourceInterceptor(
            new ResourceAuthorizationService(
                invocation -> {
                  invocations.add(invocation);
                  return ResourceAuthorizationDecision.allowed(invocation.resourceCode());
                }));

    boolean handled =
        interceptor.preHandle(
            new MockHttpServletRequest(),
            new MockHttpServletResponse(),
            new HandlerMethod(new DemoController(), "plain"));

    assertThat(handled).isTrue();
    assertThat(invocations).isEmpty();
  }

  @Test
  void throwsWhenResourceDenied() throws Exception {
    RequiresResourceInterceptor interceptor =
        new RequiresResourceInterceptor(
            new ResourceAuthorizationService(
                invocation ->
                    ResourceAuthorizationDecision.denied(
                        invocation.resourceCode(), "RESOURCE_NOT_GRANTED")));

    assertThatThrownBy(
            () ->
                interceptor.preHandle(
                    new MockHttpServletRequest(),
                    new MockHttpServletResponse(),
                    new HandlerMethod(new DemoController(), "meeting")))
        .isInstanceOf(ResourceAuthorizationException.class)
        .extracting(exception -> ((ResourceAuthorizationException) exception).decision().reason())
        .isEqualTo("RESOURCE_NOT_GRANTED");
  }

  private static final class DemoController {

    @RequiresResource("rsc:meeting:view")
    public void meeting() {
    }

    @RequiresResource(RequiresResource.NONE)
    public void health() {
    }

    public void plain() {
    }
  }
}
