package com.luokuiai.forga.core.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

class CoreReferenceTest {

  @Test
  void createsOpaqueObjectAndSubjectReferences() {
    ObjectRef object = new ObjectRef("document", " space:doc-123 ");
    SubjectRef subject = new SubjectRef("principal", " alpha:subject-456 ");

    assertThat(object.type()).isEqualTo("document");
    assertThat(object.id()).isEqualTo("space:doc-123");
    assertThat(subject.type()).isEqualTo("principal");
    assertThat(subject.id()).isEqualTo("alpha:subject-456");
  }

  @Test
  void createsNamedPolicyReferences() {
    assertThat(new RelationRef("parent.viewer").name()).isEqualTo("parent.viewer");
    assertThat(new PermissionRef("document:read").name()).isEqualTo("document:read");
    assertThat(new CaveatRef("within-hours").name()).isEqualTo("within-hours");
    assertThat(new AttributeRef("request.ip").name()).isEqualTo("request.ip");
  }

  @Test
  void createsSubjectSetReferences() {
    ObjectRef object = new ObjectRef("folder", "folder-1");
    RelationRef relation = new RelationRef("viewer");

    SubjectSetRef subjectSet = new SubjectSetRef(object, relation);

    assertThat(subjectSet.object()).isEqualTo(object);
    assertThat(subjectSet.relation()).isEqualTo(relation);
  }

  @Test
  void createsConsistencyTokens() {
    assertThat(new ConsistencyToken(" snapshot-42 ").value()).isEqualTo("snapshot-42");
  }

  @Test
  void rejectsBlankRequiredValues() {
    assertThatIllegalArgumentException().isThrownBy(() -> new ObjectRef(" ", "object-1"));
    assertThatIllegalArgumentException().isThrownBy(() -> new ObjectRef("document", " "));
    assertThatIllegalArgumentException().isThrownBy(() -> new ConsistencyToken(""));
  }

  @Test
  void rejectsUnsupportedKindCharacters() {
    assertThatIllegalArgumentException().isThrownBy(() -> new RelationRef("1viewer"));
    assertThatIllegalArgumentException().isThrownBy(() -> new RelationRef("view/owner"));
  }

  @Test
  void rejectsControlCharacters() {
    assertThatIllegalArgumentException().isThrownBy(() -> new SubjectRef("principal", "a\nb"));
  }

  @Test
  void rejectsNullSubjectSetParts() {
    RelationRef relation = new RelationRef("viewer");
    ObjectRef object = new ObjectRef("folder", "folder-1");

    assertThatNullPointerException().isThrownBy(() -> new SubjectSetRef(null, relation));
    assertThatNullPointerException().isThrownBy(() -> new SubjectSetRef(object, null));
  }
}
