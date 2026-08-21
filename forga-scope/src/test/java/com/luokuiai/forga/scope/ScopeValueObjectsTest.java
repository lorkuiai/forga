package com.luokuiai.forga.scope;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import com.luokuiai.forga.core.model.ObjectRef;
import com.luokuiai.forga.core.model.SubjectRef;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ScopeValueObjectsTest {

  @Test
  void createsActiveScopeAndObjectReference() {
    ScopeRef scope = new ScopeRef("workspace", "alpha");
    ActiveScope activeScope = new ActiveScope(scope);

    assertThat(scope.toObjectRef()).isEqualTo(new ObjectRef("workspace", "alpha"));
    assertThat(activeScope.scope()).isEqualTo(scope);
    assertThat(activeScope.attributes()).isEmpty();
  }

  @Test
  void rejectsInvalidScopeReference() {
    assertThatIllegalArgumentException().isThrownBy(() -> new ScopeRef("", "alpha"));
    assertThatIllegalArgumentException().isThrownBy(() -> new ScopeRef("bad type", "alpha"));
    assertThatIllegalArgumentException().isThrownBy(() -> new ScopeRef("workspace", " "));
  }

  @Test
  void createsScopedSubjectWithOptionalScope() {
    SubjectRef subject = new SubjectRef("principal", "alice");
    ActiveScope activeScope = new ActiveScope(new ScopeRef("workspace", "alpha"));

    assertThat(ScopedSubject.of(subject, activeScope).activeScope()).contains(activeScope);
    assertThat(ScopedSubject.withoutScope(subject).activeScope()).isEmpty();
  }

  @Test
  void exposesActingScopeContext() {
    SubjectRef original = new SubjectRef("principal", "alice");
    SubjectRef acting = new SubjectRef("principal", "service");
    ActiveScope activeScope = new ActiveScope(new ScopeRef("workspace", "alpha"));
    ActingScopeContext context = new ActingScopeContext(original, acting, activeScope);

    assertThat(context.actingAs()).isTrue();
    assertThat(context.scopedSubject())
        .isEqualTo(new ScopedSubject(acting, Optional.of(activeScope)));
  }

  @Test
  void providerContractsReturnOptionalValues() {
    ActiveScope activeScope = new ActiveScope(new ScopeRef("workspace", "alpha"));
    ScopedSubject scopedSubject =
        ScopedSubject.of(new SubjectRef("principal", "alice"), activeScope);
    ActiveScopeProvider activeProvider = () -> Optional.of(activeScope);
    ScopedSubjectProvider subjectProvider = () -> Optional.of(scopedSubject);

    assertThat(activeProvider.activeScope()).contains(activeScope);
    assertThat(subjectProvider.scopedSubject()).contains(scopedSubject);
  }
}
