## MODIFIED Requirements

### Requirement: Active Scope Permission Checks

The SDK SHALL provide a service method for checking permissions against the current active scope. Strict scoped checks MUST bind the protected object to a host-resolved owning scope before evaluating the object permission.

#### Scenario: Allowed permission in matching active scope

- **WHEN** the subject may enter the active scope, the object's owning scope matches the active scope, and the subject has the requested object permission
- **THEN** the service returns allowed

#### Scenario: Missing active scope

- **WHEN** a scope-bound permission check is requested without an active scope
- **THEN** the service returns a fail-closed denied result

#### Scenario: Object scope is unresolved

- **WHEN** a strict scoped check cannot resolve the protected object's owning scope
- **THEN** the service returns denied and MUST NOT evaluate the object permission

#### Scenario: Permission isolated by scope

- **WHEN** the object's owning scope differs from the active scope and no explicit cross-scope grant exists
- **THEN** the service returns denied and MUST NOT evaluate the object permission

#### Scenario: Object scope resolver fails

- **WHEN** the object scope resolver fails while evaluating a strict scoped check
- **THEN** the service returns a fail-closed resolver-failure decision

### Requirement: Scoped Query Constraints

The SDK SHALL provide typed query constraints that bind protected queries to the active scope and SHALL allow composition with a host-provided set-based cross-scope grant constraint.

#### Scenario: Apply scope constraint

- **WHEN** a protected query is executed with an active scope
- **THEN** the generated query constraint includes parameterized predicates for the active scope fields

#### Scenario: Apply active-or-granted constraint

- **WHEN** a host provides a typed cross-scope grant constraint
- **THEN** the generated query constraint allows rows in the active scope or rows matched by the explicit grant constraint

#### Scenario: Disabled scope integration

- **WHEN** scope query integration is disabled
- **THEN** ordinary business queries execute unchanged

## ADDED Requirements

### Requirement: Explicit Cross-Scope Authorization

The SDK SHALL provide a host resolver contract for explicitly authorizing an object permission when the active scope differs from the object's owning scope.

#### Scenario: Cross-scope grant allows evaluation

- **WHEN** the subject may enter the active scope, the owning scope differs, and the cross-scope resolver explicitly allows the exact object permission request
- **THEN** the service evaluates the subject's ordinary object permission and returns that decision

#### Scenario: Cross-scope grant denied

- **WHEN** the owning scope differs and the cross-scope resolver does not explicitly allow the request
- **THEN** the service returns denied

#### Scenario: Cross-scope resolver fails

- **WHEN** the cross-scope resolver fails
- **THEN** the service returns a fail-closed resolver-failure decision

#### Scenario: Same-scope request avoids grant lookup

- **WHEN** the object's owning scope equals the active scope
- **THEN** the service proceeds without invoking the cross-scope resolver
