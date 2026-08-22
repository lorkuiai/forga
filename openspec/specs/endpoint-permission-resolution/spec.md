# endpoint-permission-resolution Specification

## Purpose
TBD - created by archiving change separate-authentication-from-authorization. Update Purpose after archive.
## Requirements
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

### Requirement: Exact external endpoint registration
Forga SHALL allow hosts to bind required-permission or explicit permit-all metadata to an exact
Spring MVC controller method using its controller type, method name, and parameter types.

#### Scenario: SDK endpoint requires permission
- **WHEN** a host registers a compiled SDK controller method with a permission definition
- **THEN** the common endpoint resolver returns that permission for the corresponding handler

#### Scenario: SDK endpoint permits all
- **WHEN** a host registers a compiled SDK controller method as permit-all
- **THEN** the common endpoint resolver returns explicit permit-all metadata

### Requirement: Startup registration validation
Forga MUST validate external registrations against Spring MVC handler methods before serving
requests and MUST reject invalid or conflicting static declarations.

#### Scenario: Exact overloaded method is registered
- **WHEN** a controller has overloaded methods and the registration supplies exact parameter types
- **THEN** Forga binds only the matching Spring handler method

#### Scenario: Registered method is not a handler
- **WHEN** a registration names a missing method or a method that is not a Spring MVC handler
- **THEN** application startup fails with the unresolved controller signature

#### Scenario: Annotation conflicts with registration
- **WHEN** one handler has annotation metadata and an external registration with different results
- **THEN** application startup fails before the handler can receive requests

### Requirement: Deterministic metadata composition
Forga SHALL compose annotation, external registration, and host resolver metadata into one endpoint
requirement and MUST fail closed when resolved results disagree.

#### Scenario: Identical declarations are composed
- **WHEN** multiple metadata sources resolve the same required permission or permit-all result
- **THEN** Forga returns the shared result once

#### Scenario: Dynamic resolver conflicts
- **WHEN** a request-dependent host resolver disagrees with static endpoint metadata
- **THEN** endpoint authorization rejects the request before invoking the handler

### Requirement: Registry-based MVC auto-configuration
The Spring Boot Starter SHALL register endpoint enforcement automatically when Forga is enabled and
endpoint contributors and an endpoint authorizer are available.

#### Scenario: Registry integration is enabled
- **WHEN** a host provides an endpoint contributor and authorizer without a custom interceptor bean
- **THEN** the Starter registers one MVC interceptor using the composed endpoint metadata

#### Scenario: Registry enforcement is incomplete
- **WHEN** a host provides an endpoint contributor without an authorizer or host interceptor
- **THEN** application startup fails before registered endpoints can receive requests

#### Scenario: Forga is disabled
- **WHEN** Forga integration is disabled
- **THEN** the Starter does not assemble registrations or add endpoint enforcement
