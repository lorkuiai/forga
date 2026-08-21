## ADDED Requirements

### Requirement: Optional permission annotation
Forga SHALL provide a method-or-type `@RequiresPermission` annotation with one required,
host-defined permission code and no no-permission sentinel value.

#### Scenario: Annotated endpoint resolves permission
- **WHEN** the default resolver inspects an endpoint annotated with `@RequiresPermission`
- **THEN** it returns the corresponding `PermissionRef`

### Requirement: Explicit permit-all metadata
The default endpoint resolver SHALL recognize Jakarta `@PermitAll` as an explicit public endpoint
declaration.

#### Scenario: Public endpoint is declared
- **WHEN** a handler method or type has `@PermitAll`
- **THEN** endpoint authorization continues without a permission decision

### Requirement: Pluggable endpoint permission resolution
Forga SHALL expose an endpoint resolver SPI that allows host annotations or centralized route
metadata to produce the same required-permission or permit-all result as the default resolver.

#### Scenario: Host annotation is adapted
- **WHEN** a host resolver recognizes host-owned endpoint metadata
- **THEN** the common interceptor enforces the resolved Forga permission

### Requirement: Unresolved endpoints fail closed
The endpoint interceptor MUST deny a Spring handler method when no resolver returns either a
permission requirement or explicit permit-all metadata.

#### Scenario: Endpoint metadata is missing
- **WHEN** an unresolved Spring handler method is intercepted
- **THEN** invocation is rejected before the handler body executes

### Requirement: Business code has no authorization calls
Endpoint enforcement SHALL invoke a configured authorizer before the handler and SHALL NOT require
controllers, services, or mappers to call a Forga authorization facade.

#### Scenario: Required permission is allowed
- **WHEN** the authorizer allows the resolved permission
- **THEN** the interceptor proceeds to ordinary controller and service code

#### Scenario: Required permission is denied
- **WHEN** the authorizer denies the resolved permission
- **THEN** the interceptor throws a stable authorization exception before business code executes

### Requirement: Legacy resource annotation removal
Forga SHALL remove the snapshot `@RequiresResource`, `NONE` marker, and programmatic
`ResourceAuthorizationService` integration APIs.

#### Scenario: Snapshot consumer migrates
- **WHEN** a host upgrades to the new snapshot
- **THEN** it uses `@RequiresPermission`, `@PermitAll`, or a host endpoint resolver instead of the
  removed resource annotation and service facade
