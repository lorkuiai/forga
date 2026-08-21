package com.luokuiai.forga.satoken;

import cn.dev33.satoken.stp.StpLogic;
import com.luokuiai.forga.core.context.AuthenticatedSubjectProvider;
import com.luokuiai.forga.core.model.SubjectRef;
import java.util.Objects;
import java.util.Optional;

/** Maps a selected Sa-Token login context to a Forga authorization subject. */
public final class SaTokenAuthenticatedSubjectProvider
    implements AuthenticatedSubjectProvider {

  private static final String USER_SUBJECT_TYPE = "user";

  private final StpLogic stpLogic;

  /**
   * Creates a Sa-Token authenticated-subject provider.
   *
   * @param stpLogic selected Sa-Token login context
   */
  public SaTokenAuthenticatedSubjectProvider(StpLogic stpLogic) {
    this.stpLogic = Objects.requireNonNull(stpLogic, "stpLogic is required");
  }

  @Override
  public Optional<SubjectRef> currentSubject() {
    if (!stpLogic.isLogin()) {
      return Optional.empty();
    }
    return Optional.of(new SubjectRef(USER_SUBJECT_TYPE, stpLogic.getLoginIdAsString()));
  }
}
