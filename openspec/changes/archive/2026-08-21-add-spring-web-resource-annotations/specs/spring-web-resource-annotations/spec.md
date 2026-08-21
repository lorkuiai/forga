## ADDED Requirements

### Requirement: Resource Annotation API

Forga SHALL provide a method-level resource annotation that accepts host-defined resource codes as strings.

#### Scenario: resource code annotation

- **GIVEN** a method annotated with a host-defined resource code
- **WHEN** Spring Web dispatch reaches the method
- **THEN** the integration can read the resource code without SDK-defined business enums

#### Scenario: explicit no-resource marker

- **GIVEN** a method annotated with the annotation `NONE` marker
- **WHEN** the interceptor processes the handler
- **THEN** no authorization check is executed

### Requirement: Host-Owned Resource Mapping

Forga SHALL require host applications to provide an adapter that maps a resource code and invocation context to Forga authorization checks.

#### Scenario: RBAC-style resource check

- **GIVEN** a host adapter maps a resource code to an application object and permission
- **WHEN** the service checks that resource code
- **THEN** the Forga evaluator decision determines allow or deny

#### Scenario: scope-aware resource check

- **GIVEN** a host adapter maps a resource code to a scoped permission request
- **WHEN** an active scope is available
- **THEN** the scoped authorization decision determines allow or deny

### Requirement: Automatic Spring MVC Guard

Forga SHALL provide a Spring MVC interceptor that enforces resource annotations on handler methods.

#### Scenario: denied resource

- **GIVEN** a handler method has a resource annotation
- **AND** the host adapter returns a denied decision
- **WHEN** the interceptor handles the request
- **THEN** it throws the SDK denial exception before the handler body runs

#### Scenario: allowed resource

- **GIVEN** a handler method has a resource annotation
- **AND** the host adapter returns an allowed decision
- **WHEN** the interceptor handles the request
- **THEN** request handling continues

### Requirement: Programmatic Resource Checks

Forga SHALL expose a programmatic resource authorization service for application-service code paths outside Spring MVC.

#### Scenario: manual guard

- **GIVEN** application code calls `requireResource`
- **WHEN** the host adapter denies the resource
- **THEN** the same denial exception is thrown
