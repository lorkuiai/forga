## Context

`ScopedAuthorizationService` currently performs a scope-entry evaluator check followed by an object-permission evaluator check. `ActiveScope` is not passed into core evaluation as an ownership boundary, and the SDK has no contract for resolving an object's owning scope. This keeps core domain-neutral but leaves scope isolation dependent on an undocumented host-side precondition.

The enhancement belongs in optional `forga-scope`. Hosts continue to own object metadata and cross-scope grants, while Forga coordinates the checks and fails closed.

## Goals / Non-Goals

**Goals:**

- Bind strict scoped permission checks to a host-resolved owning scope.
- Allow cross-scope checks only after an explicit host resolver grant.
- Preserve scope-entry and object-permission evaluator semantics.
- Distinguish ordinary denial from resolver failure through existing stable `DecisionReason` values.
- Provide set-based query composition for same-scope or explicitly granted rows.
- Preserve source compatibility for existing service construction.

**Non-Goals:**

- Define tenant, organization, employment, position, or grant persistence models.
- Add scope-specific concepts to `forga-core`.
- Compile cross-scope grants into recursive SQL or perform per-row evaluator checks.
- Make every Forga authorization request scope-bound.

## Decisions

1. Resolve object ownership through `ObjectScopeResolver`.

   The resolver accepts an `ObjectRef` and returns an optional `ScopeRef`. An empty, null, or failed resolution denies a strict scoped check. This contract allows hosts to resolve ownership from tables, caches, or services without Forga owning object data.

2. Represent cross-scope authorization through `CrossScopeAccessResolver`.

   The resolver receives an immutable `CrossScopeAccessRequest` containing the active scope, object scope, object, permission, subject, and request attributes. It is invoked only when active and owning scopes differ. Returning false, returning null where prohibited, or throwing fails closed.

3. Keep core decisions stable.

   Boundary mismatch and missing explicit grants return `DecisionReason.NO_MATCH`. Resolver exceptions return `DecisionReason.RESOLVER_FAILURE`. Scope-specific reasons are not added to `forga-core`.

4. Add strict constructors without silently changing legacy behavior.

   Existing constructors remain available because they cannot infer object ownership. New constructors accepting `ObjectScopeResolver` enable strict same-scope enforcement and deny cross-scope access by default; an additional resolver enables explicit grants. Documentation will direct new scoped integrations to strict construction.

5. Compose list constraints rather than checking rows.

   `ScopeQueryConstraints.activeOrGranted` combines the existing active-scope constraint with a host-supplied typed grant constraint using OR. The host remains responsible for a bounded, set-based grant predicate or authorized rowset.

## Authorization Flow

1. Reject a missing active scope.
2. Evaluate permission to enter the active scope.
3. In strict mode, resolve the object's owning scope.
4. Continue when owning and active scopes match.
5. When they differ, require `CrossScopeAccessResolver` to allow the exact request.
6. Evaluate the subject's requested permission on the object.

Every failed or incomplete prerequisite stops evaluation and denies access.

## Risks / Trade-offs

- [Risk] Legacy constructors still permit host-managed object binding. -> Mitigation: document them as compatibility mode and use strict constructors in examples.
- [Risk] A host cross-scope resolver may be too permissive. -> Mitigation: pass the exact object, permission, subject, and both scopes; require the ordinary object-permission check afterward.
- [Risk] Single-object and list semantics can drift. -> Mitigation: provide typed active-or-granted query composition and document that the host grant constraint must mirror the cross-scope resolver.
- [Risk] Resolver calls add latency. -> Mitigation: strict checks perform at most one object-scope lookup and, only for mismatches, one grant lookup; hosts may cache behind their contracts.
- [Risk] Scope entry, object ownership, grants, and object permission can observe different concurrent host states. -> Mitigation: resolver contracts require a shared request-consistent host snapshot; hosts should bind implementations to the same transaction or consistency context.
