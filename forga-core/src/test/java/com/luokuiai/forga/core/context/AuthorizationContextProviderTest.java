package com.luokuiai.forga.core.context;

import static org.assertj.core.api.Assertions.assertThat;

import com.luokuiai.forga.core.model.AttributeRef;
import com.luokuiai.forga.core.model.SubjectRef;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AuthorizationContextProviderTest {

  @Test
  void suppliesNeutralSubjectAndAttributes() {
    AuthenticatedSubjectProvider subjects =
        () -> Optional.of(new SubjectRef("account", "alice"));
    AuthorizationAttributesProvider attributes =
        () -> Map.of(new AttributeRef("request.ip"), "127.0.0.1");

    assertThat(subjects.currentSubject()).contains(new SubjectRef("account", "alice"));
    assertThat(attributes.attributes())
        .containsEntry(new AttributeRef("request.ip"), "127.0.0.1");
  }

  @Test
  void representsMissingAuthenticationAsEmpty() {
    AuthenticatedSubjectProvider subjects = Optional::empty;

    assertThat(subjects.currentSubject()).isEmpty();
  }
}
