package com.luokuiai.forga.spring.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ResourceAuthorizationServiceTest {

  @Test
  void requireResourceAllowsGrantedDecision() {
    List<String> checkedResources = new ArrayList<>();
    ResourceAuthorizationService service =
        new ResourceAuthorizationService(
            invocation -> {
              checkedResources.add(invocation.resourceCode());
              return ResourceAuthorizationDecision.allowed(invocation.resourceCode());
            });

    service.requireResource("rsc:meeting:view");

    assertThat(checkedResources).containsExactly("rsc:meeting:view");
  }

  @Test
  void requireResourceThrowsForDeniedDecision() {
    ResourceAuthorizationService service =
        new ResourceAuthorizationService(
            invocation ->
                ResourceAuthorizationDecision.denied(
                    invocation.resourceCode(), "RESOURCE_NOT_GRANTED"));

    assertThatThrownBy(() -> service.requireResource("rsc:meeting:maintain"))
        .isInstanceOf(ResourceAuthorizationException.class)
        .extracting(exception -> ((ResourceAuthorizationException) exception).decision().reason())
        .isEqualTo("RESOURCE_NOT_GRANTED");
  }

  @Test
  void hasResourceReturnsDecisionAllowedState() {
    ResourceAuthorizationService service =
        new ResourceAuthorizationService(
            invocation ->
                ResourceAuthorizationDecision.denied(
                    invocation.resourceCode(), "RESOURCE_NOT_GRANTED"));

    assertThat(service.hasResource("rsc:meeting:view")).isFalse();
  }

  @Test
  void nullAdapterDecisionFailsClosed() {
    ResourceAuthorizationService service = new ResourceAuthorizationService(invocation -> null);

    assertThatThrownBy(() -> service.requireResource("rsc:file:view"))
        .isInstanceOf(ResourceAuthorizationException.class)
        .extracting(exception -> ((ResourceAuthorizationException) exception).decision().reason())
        .isEqualTo("ADAPTER_RETURNED_NULL");
  }
}
