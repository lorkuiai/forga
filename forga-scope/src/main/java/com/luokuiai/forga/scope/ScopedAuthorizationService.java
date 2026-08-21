package com.luokuiai.forga.scope;

import com.luokuiai.forga.core.eval.AuthorizationEvaluator;
import com.luokuiai.forga.core.eval.CheckDecision;
import com.luokuiai.forga.core.eval.CheckRequest;
import com.luokuiai.forga.core.eval.DecisionReason;
import com.luokuiai.forga.core.model.PermissionRef;
import java.util.Objects;
import java.util.Optional;

/**
 * Service facade for scope switch and active-scope permission checks.
 */
public final class ScopedAuthorizationService {

  private final AuthorizationEvaluator evaluator;

  private final PermissionRef scopeEntryPermission;

  /**
   * Creates a scoped authorization service using the default scope entry permission.
   *
   * @param evaluator evaluator used for underlying authorization checks
   */
  public ScopedAuthorizationService(AuthorizationEvaluator evaluator) {
    this(evaluator, ScopePolicyTemplates.ENTER);
  }

  /**
   * Creates a scoped authorization service.
   *
   * @param evaluator evaluator used for underlying authorization checks
   * @param scopeEntryPermission permission required on the active scope before resource checks
   */
  public ScopedAuthorizationService(
      AuthorizationEvaluator evaluator, PermissionRef scopeEntryPermission) {
    this.evaluator = Objects.requireNonNull(evaluator, "evaluator is required");
    this.scopeEntryPermission =
        Objects.requireNonNull(scopeEntryPermission, "scope entry permission is required");
  }

  /**
   * Checks whether the subject can enter the target scope.
   *
   * @param request switch request
   * @return switch decision
   */
  public ScopeSwitchDecision canSwitch(ScopeSwitchRequest request) {
    Objects.requireNonNull(request, "request is required");
    CheckDecision decision =
        evaluator.check(
            new CheckRequest(
                request.targetScope().toObjectRef(),
                request.permission(),
                request.subject(),
                request.attributes()));
    Optional<ActiveScope> activeScope =
        decision.allowed() ? Optional.of(new ActiveScope(request.targetScope())) : Optional.empty();
    return new ScopeSwitchDecision(request, decision, activeScope);
  }

  /**
   * Checks a permission requiring an active scope.
   *
   * @param request scoped permission request
   * @return scoped permission decision
   */
  public ScopedPermissionDecision check(ScopedPermissionRequest request) {
    Objects.requireNonNull(request, "request is required");
    if (request.activeScope().isEmpty()) {
      CheckRequest checkRequest =
          new CheckRequest(
              request.object(),
              request.permission(),
              request.subject(),
              request.attributes());
      return new ScopedPermissionDecision(
          request, new CheckDecision(checkRequest, false, DecisionReason.NO_MATCH));
    }
    ActiveScope activeScope = request.activeScope().orElseThrow();
    CheckDecision scopeDecision =
        evaluator.check(
            new CheckRequest(
                activeScope.scope().toObjectRef(),
                scopeEntryPermission,
                request.subject(),
                request.attributes()));
    if (!scopeDecision.allowed()) {
      return new ScopedPermissionDecision(request, scopeDecision);
    }
    CheckDecision decision =
        evaluator.check(
            new CheckRequest(
                request.object(), request.permission(), request.subject(), request.attributes()));
    return new ScopedPermissionDecision(request, decision);
  }
}
