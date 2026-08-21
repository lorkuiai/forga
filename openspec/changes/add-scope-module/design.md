## Context

Forga already supports RBAC, ReBAC, ABAC evaluation and optional MyBatis/Spring integration. Applications that operate across multiple authorization boundaries still need a consistent way to represent the active boundary for a request, decide whether a subject can switch into another boundary, and ensure permission checks and query constraints are evaluated against that active boundary.

`forga-scope` adds this pattern as an optional module. Host applications continue to own persistence and business naming; the module only provides neutral scope references, request state, policy templates, and service contracts that compose with the existing evaluator and resolver APIs.

## Goals / Non-Goals

**Goals:**

- Provide `ScopeRef`, `ActiveScope`, `ScopedSubject`, and switch request/result value objects.
- Provide policy template helpers for membership, role assignment, and denial patterns.
- Provide a scoped authorization service for switch checks and active-scope permission checks.
- Provide provider contracts that Spring/MyBatis integration can use to read active scope without knowing host application models.
- Keep batch APIs bounded and resolver-backed so scoped list checks do not create N+1 resolver calls.

**Non-Goals:**

- Define storage schemas, persistence repositories, or migrations.
- Define host business entities or business-specific role names.
- Replace direct use of RBAC/ReBAC/ABAC APIs for applications that do not need scoped boundaries.

## Decisions

1. Use `scope` as the module and API vocabulary.

   `scope` names the authorization boundary without binding the API to a business domain. Alternatives considered were `boundary`, `realm`, `context`, and `tenancy`. `boundary` is clear for isolation but awkward in request APIs; `realm` has authentication connotations; `context` is too broad; `tenancy` is too narrow.

2. Model active scope separately from subject.

   A subject can have different relations in different scopes. `ScopedSubject` will combine a `SubjectRef` with an optional `ActiveScope` for APIs that need a single request principal, while `ActiveScope` remains available independently for providers and query filters.

3. Express switch authorization as a normal permission check.

   Scope switch checks will compile to ordinary object/permission checks where the target scope is represented as an `ObjectRef` and the switch permission is supplied by a template. This keeps switch logic composable with existing resolvers and caveats.

4. Provide templates, not mandatory policies.

   The module will expose builders/constants for common membership, assignment, and denial expressions. Host applications can use the templates as-is or compose lower-level expressions themselves.

5. Keep scoped query constraints explicit and bounded.

   Query integration will expose active scope attributes through provider contracts and generated query constraints. Collection APIs must use resolver batch methods and evaluator bounds where available, preserving existing traversal limits and avoiding per-row relationship checks.

## Risks / Trade-offs

- [Risk] `scope` can feel abstract to business integrators. → Mitigation: README/API docs will describe mapping examples for authorization boundaries without encoding those names into the API.
- [Risk] Templates may become too opinionated. → Mitigation: First version only includes minimal membership, assignment, and denial primitives.
- [Risk] Scoped checks can be misused without active scope. → Mitigation: service methods that require an active scope fail closed when it is absent.
- [Risk] Query filtering can drift from check semantics. → Mitigation: tests will cover both switch/check results and the generated scoped query constraints for the same active scope.
