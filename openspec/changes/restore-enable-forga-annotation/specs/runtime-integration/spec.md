## MODIFIED Requirements

### Requirement: Opt-in runtime assembly
Framework integrations MUST register authorization components only when a host composition root
explicitly declares `@EnableForga`. Environment properties, including `forga.enabled`, MUST NOT
enable or disable Forga assembly. Enabled integration MUST validate policies, resolver capabilities,
authentication providers, and duplicate registrations before serving requests.

#### Scenario: Composition root enables Forga
- **WHEN** a host configuration class declares `@EnableForga`
- **THEN** Forga integration components and the versioned startup banner are registered
- **AND** incomplete required infrastructure causes application startup to fail with a precise
  configuration error

#### Scenario: Legacy property attempts to enable Forga
- **WHEN** the Starter is present and `forga.enabled=true` is configured without `@EnableForga`
- **THEN** Forga integration components and the startup banner MUST remain absent

### Requirement: Disabled behavior preserves business queries
When no host composition root declares `@EnableForga`, integrations MUST NOT install authorization
interceptors, mutate query context, alter SQL produced by business persistence code, or emit an
enabled Forga startup banner.

#### Scenario: Unannotated application runs a list query
- **WHEN** the Starter is present without `@EnableForga` and a business Mapper executes its normal
  query
- **THEN** the query executes without a Forga predicate, join, or required context

### Requirement: Integration rollback
Hosts MUST be able to remove `@EnableForga`, restart, and return to their original query and
authorization path without a Forga data migration.

#### Scenario: Host rolls back integration
- **WHEN** `@EnableForga` is removed from the composition root and the application restarts
- **THEN** Forga integration components are absent and no relationship data rollback is required
