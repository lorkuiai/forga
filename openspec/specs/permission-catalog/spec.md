# permission-catalog Specification

## Purpose
TBD - created by archiving change separate-authentication-from-authorization. Update Purpose after archive.
## Requirements
### Requirement: Immutable permission definitions
Forga SHALL model stable host-defined permissions as immutable definitions containing a validated
`PermissionRef` and host-facing metadata without assigning business meaning to the values.

#### Scenario: Permission definition is valid
- **WHEN** a host creates a definition with a permission, display name, and module
- **THEN** Forga preserves the normalized immutable definition

#### Scenario: Permission definition is invalid
- **WHEN** required permission metadata is null or blank
- **THEN** construction fails before catalog synchronization

### Requirement: Composable permission catalog
Forga SHALL assemble permission definitions from independent contributors and reject duplicate
permission references.

#### Scenario: Module contributions are assembled
- **WHEN** several host modules contribute distinct permissions
- **THEN** the catalog exposes one deterministic immutable collection

#### Scenario: Duplicate permission is contributed
- **WHEN** two definitions use the same `PermissionRef`
- **THEN** catalog assembly fails with the duplicate permission name

### Requirement: Host-owned synchronization
Forga SHALL expose a synchronization contract that hands an immutable permission catalog to host
infrastructure without defining tables, deletion rules, assignments, or transaction behavior.

#### Scenario: Host persists catalog
- **WHEN** Spring Boot assembles an enabled catalog and a host synchronizer is available
- **THEN** the host can upsert definitions while retaining ownership of persistence semantics

### Requirement: Catalog is not endpoint discovery
The permission catalog MUST remain independent of endpoint scanning so permissions used by jobs,
messages, or future entry points can be persisted even when no Controller references them.

#### Scenario: Permission has no Web endpoint
- **WHEN** a contributor declares a permission not referenced by Spring Web metadata
- **THEN** the permission remains present in the catalog for host assignment

### Requirement: Endpoint-contributed permission definitions
Spring Web endpoint registrations that require permissions SHALL contribute their definitions to the
ordinary permission catalog without adding Spring or endpoint metadata to the core catalog model.

#### Scenario: Registered permission enters catalog
- **WHEN** an endpoint contributor registers a required permission definition
- **THEN** catalog assembly includes that definition for host synchronization

#### Scenario: Permission protects several endpoints
- **WHEN** several endpoint registrations use the same identical permission definition
- **THEN** catalog assembly contains the definition once

#### Scenario: Endpoint definitions conflict
- **WHEN** endpoint registrations use one permission reference with different display or module data
- **THEN** registration assembly fails before catalog synchronization

#### Scenario: Permit-all endpoint is registered
- **WHEN** an endpoint contributor declares a handler as permit-all
- **THEN** no permission definition is added to the catalog for that declaration
