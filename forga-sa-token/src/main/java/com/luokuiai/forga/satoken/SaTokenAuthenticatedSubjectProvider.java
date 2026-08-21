package com.luokuiai.forga.satoken;

import cn.dev33.satoken.stp.StpLogic;
import com.luokuiai.forga.core.context.AuthenticatedSubjectProvider;
import com.luokuiai.forga.core.model.SubjectRef;
import java.util.Objects;
import java.util.Optional;

/** Maps a selected Sa-Token login context to a Forga authorization subject. */
public final class SaTokenAuthenticatedSubjectProvider
    implements AuthenticatedSubjectProvider {

  private final StpLogic stpLogic;

  private final String subjectType;

  /**
   * Creates a Sa-Token authenticated-subject provider.
   *
   * @param stpLogic selected Sa-Token login context
   * @param subjectType caller-defined Forga subject type
   */
  public SaTokenAuthenticatedSubjectProvider(StpLogic stpLogic, String subjectType) {
    this.stpLogic = Objects.requireNonNull(stpLogic, "stpLogic is required");
    this.subjectType = validateSubjectType(subjectType);
  }

  @Override
  public Optional<SubjectRef> currentSubject() {
    if (!stpLogic.isLogin()) {
      return Optional.empty();
    }
    return Optional.of(new SubjectRef(subjectType, stpLogic.getLoginIdAsString()));
  }

  private static String validateSubjectType(String subjectType) {
    return new SubjectRef(subjectType, "validation").type();
  }
}
