## MODIFIED Requirements

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
