package com.luokuiai.forga.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import com.luokuiai.forga.core.model.ObjectRef;
import com.luokuiai.forga.core.model.RelationRef;
import com.luokuiai.forga.core.model.SubjectRef;
import com.luokuiai.forga.core.model.SubjectSetRef;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class RelationshipContractTest {

  @Test
  void createsForwardRequestsAndResponses() {
    ForwardRelationshipRequest request =
        new ForwardRelationshipRequest(new ObjectRef("document", "doc-1"), relation(), 10);
    DirectSubject direct = new DirectSubject(new SubjectRef("principal", "subject-1"));
    SubjectSetSubject subjectSet =
        new SubjectSetSubject(
            new SubjectSetRef(new ObjectRef("group", "group-1"), new RelationRef("member")));

    ForwardRelationshipResponse response =
        new ForwardRelationshipResponse(request, List.of(direct, subjectSet));

    assertThat(response.request()).isEqualTo(request);
    assertThat(response.subjects()).containsExactly(direct, subjectSet);
  }

  @Test
  void createsReverseRequestsAndResponses() {
    ReverseRelationshipRequest request =
        new ReverseRelationshipRequest(
            "document",
            relation(),
            new DirectSubject(new SubjectRef("principal", "subject-1")),
            10);

    ReverseRelationshipResponse response =
        new ReverseRelationshipResponse(
            request,
            List.of(new ObjectRef("document", "doc-1")),
            Optional.of(new PageCursor("next")),
            ConsistencyContext.empty());

    assertThat(response.request()).isEqualTo(request);
    assertThat(response.objects()).containsExactly(new ObjectRef("document", "doc-1"));
    assertThat(response.nextCursor()).contains(new PageCursor("next"));
  }

  @Test
  void copiesBatchRequests() {
    ForwardRelationshipRequest request =
        new ForwardRelationshipRequest(new ObjectRef("document", "doc-1"), relation(), 10);
    List<ForwardRelationshipRequest> requests = new ArrayList<>(List.of(request));

    ForwardRelationshipBatchRequest batch = new ForwardRelationshipBatchRequest(requests);
    requests.clear();

    assertThat(batch.requests()).containsExactly(request);
    assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(() -> batch.requests().clear());
  }

  @Test
  void enforcesRequestAndBatchBounds() {
    ObjectRef object = new ObjectRef("document", "doc-1");

    assertThatIllegalArgumentException()
        .isThrownBy(() -> new ForwardRelationshipRequest(object, relation(), 0));
    assertThatIllegalArgumentException()
        .isThrownBy(() -> new ForwardRelationshipRequest(object, relation(), 1001));
    assertThatIllegalArgumentException()
        .isThrownBy(() -> new ForwardRelationshipBatchRequest(List.of()));
  }

  @Test
  void rejectsResponsesThatExceedRequestLimits() {
    ForwardRelationshipRequest request =
        new ForwardRelationshipRequest(new ObjectRef("document", "doc-1"), relation(), 1);

    assertThatIllegalArgumentException()
        .isThrownBy(
            () ->
                new ForwardRelationshipResponse(
                    request,
                    List.of(
                        new DirectSubject(new SubjectRef("principal", "one")),
                        new DirectSubject(new SubjectRef("principal", "two")))));
  }

  @Test
  void rejectsNullParts() {
    assertThatNullPointerException().isThrownBy(() -> new DirectSubject(null));
    assertThatNullPointerException().isThrownBy(() -> new SubjectSetSubject(null));
    assertThatNullPointerException()
        .isThrownBy(() -> new ForwardRelationshipRequest(null, relation(), 10));
    assertThatNullPointerException()
        .isThrownBy(
            () -> new ForwardRelationshipRequest(new ObjectRef("document", "doc-1"), null, 10));
  }

  private static RelationRef relation() {
    return new RelationRef("viewer");
  }
}
