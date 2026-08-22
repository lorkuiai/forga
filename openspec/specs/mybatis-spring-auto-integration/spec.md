# mybatis-spring-auto-integration Specification

## Purpose
TBD - created by archiving change add-mybatis-spring-auto-integration. Update Purpose after archive.
## Requirements
### Requirement: Statement metadata driven authorization
The integration MUST apply authorization only at statement ids explicitly registered with neutral
statement metadata.

#### Scenario: Configured statement is authorized
- **WHEN** a configured MyBatis statement executes while Forga is enabled
- **THEN** the integration applies one composed typed authorization constraint for that statement

#### Scenario: Unconfigured statement executes
- **WHEN** a statement id has no authorization metadata
- **THEN** the integration leaves the SQL unchanged

### Requirement: Generic request providers
The integration MUST obtain the current subject and request attributes through neutral provider
interfaces and MUST NOT define host-domain authentication context.

#### Scenario: Subject is available
- **WHEN** a configured statement executes while a subject provider returns a subject
- **THEN** authorization proceeds using neutral SDK references

#### Scenario: Subject is missing
- **WHEN** a configured statement executes while Forga is enabled and no subject is available
- **THEN** the integration fails closed before SQL execution

### Requirement: Conditional Spring registration
The Spring integration MUST register MyBatis authorization components only when a host composition
root explicitly declares `@EnableForga`. Environment properties MUST NOT alter this registration
decision.

#### Scenario: Integration disabled
- **WHEN** the Starter is present without `@EnableForga`
- **THEN** no MyBatis authorization interceptor is registered and no request context is required

#### Scenario: Legacy property is present
- **WHEN** `forga.enabled=true` is configured without `@EnableForga`
- **THEN** no MyBatis authorization interceptor is registered

#### Scenario: Integration explicitly enabled
- **WHEN** a host composition root declares `@EnableForga` and required MyBatis infrastructure is
  complete
- **THEN** the Forga MyBatis authorization interceptor is registered
- **AND** registration does not require a separate integration properties bean

### Requirement: Safe SQL rewriting
The MyBatis integration MUST append at most one translated authorization predicate to supported
SELECT SQL and MUST reject unsupported SQL while enabled.

#### Scenario: Supported select query
- **WHEN** a configured SELECT statement executes
- **THEN** the integration appends one parameterized authorization predicate

#### Scenario: Unsupported query shape
- **WHEN** a configured non-SELECT statement executes while enabled
- **THEN** the integration fails closed before SQL execution
