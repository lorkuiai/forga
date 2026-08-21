## Why

Applications that support multiple authorization boundaries need a reusable way to model the currently active boundary and assignments across boundaries. Without a shared pattern, each host application must separately implement boundary switching, active-scope checks, and scoped query constraints, which increases the risk of inconsistent authorization behavior.

## What Changes

- Add a new optional `forga-scope` module under `com.luokuiai.forga.scope`.
- Provide domain-neutral value objects for scope references, active scope state, scope subjects, switch requests, and acting context.
- Provide policy template helpers for common scope membership, role assignment, and denial patterns.
- Provide a scoped authorization service that can answer whether a subject can enter a target scope and whether a permission is allowed in the active scope.
- Provide adapter contracts that allow Spring/MyBatis integrations to obtain the active scope and apply scope-bound query constraints.
- Non-goal: this change does not define persistence tables, business tenant models, user models, or organization models.
- Non-goal: this change does not make scope required for applications that only need direct RBAC/ReBAC checks.

## Capabilities

### New Capabilities

- `scope-authorization`: Reusable authorization patterns for scoped boundaries, active scope switching, acting context, and scope-bound permission checks.

### Modified Capabilities

- None.

## Impact

- Adds a new Gradle module: `forga-scope`.
- Adds public APIs in package `com.luokuiai.forga.scope`.
- Adds tests for scope switching, assigned access, and scope-bound permission checks.
- May add optional integration hooks in existing Spring/MyBatis modules so applications can pass active scope into generic authorization assembly.
