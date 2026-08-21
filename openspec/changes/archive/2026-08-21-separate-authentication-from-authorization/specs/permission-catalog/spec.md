## ADDED Requirements

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
