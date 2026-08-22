## ADDED Requirements

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
