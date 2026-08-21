## Why

A scoped permission check currently proves that a subject can enter the active scope and separately proves the requested object permission. It does not bind the protected object to that active scope. A subject assigned in multiple scopes can therefore satisfy a permission through one scope while another scope is active unless every host adds an additional boundary check.

## What Changes

- Add an optional host-owned object scope resolver contract to `forga-scope`.
- Add an explicit cross-scope access resolver contract and immutable request model.
- Add strict `ScopedAuthorizationService` constructors that resolve the object's owning scope, allow same-scope checks, and require an explicit grant for cross-scope checks.
- Fail closed when object scope resolution is absent, invalid, or fails, and when cross-scope access resolution denies or fails.
- Preserve existing constructors for source compatibility while documenting that they do not bind objects to scopes.
- Add a query helper that combines the active-scope predicate with a host-provided set-based grant constraint.
- Non-goal: define tenant models, relationship tables, grant persistence, or business role names.
- Non-goal: change the domain-neutral evaluator or make scope mandatory for direct core authorization checks.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `scope-authorization`: Bind scoped object checks to the active scope and support explicit cross-scope access without prescribing host storage.

## Impact

- Adds public resolver and request APIs in `com.luokuiai.forga.scope`.
- Extends `ScopedAuthorizationService` with strict constructor overloads.
- Extends `ScopeQueryConstraints` with active-or-granted composition.
- Adds scope boundary, failure, compatibility, and query constraint tests.
- Updates scope integration documentation and migration guidance.
