## ADDED Requirements

### Requirement: Opt-in runtime assembly
Framework integrations MUST register authorization components only when explicitly enabled and MUST
validate policies, resolver capabilities, and duplicate registrations before serving requests.

#### Scenario: Enabled configuration is incomplete
- **WHEN** an enabled policy references a missing resolver or unsupported reverse capability
- **THEN** application startup fails with a precise configuration error

### Requirement: Integration boundaries preserve core neutrality
Runtime integrations MUST keep host-domain semantics in host adapters, policies, resolver
implementations, and query mappings. They MUST NOT require Forga core or public API types to expose
tenant, user, person, organization, workflow, meeting, todo, appointment, proxy, mapped, or borrowed
concepts.

#### Scenario: Host registers a tenant-aware resolver
- **WHEN** a host resolver uses tenant or organization data to answer relationship or attribute
  requests
- **THEN** the tenant-aware logic remains inside the resolver or host policy
- **AND** Forga core receives only opaque subjects, objects, relations, attributes, and structured
  resolver results

### Requirement: Disabled behavior preserves business queries
When Forga is disabled, integrations MUST NOT install authorization interceptors, mutate query
context, or alter SQL produced by business persistence code.

#### Scenario: Disabled application runs a list query
- **WHEN** Forga is disabled and a business Mapper executes its normal query
- **THEN** the query executes without a Forga predicate, join, or required context

### Requirement: MyBatis constraint application
The MyBatis integration MUST apply at most one composed, parameterized authorization constraint at
a declared query boundary and MUST reject unsupported query shapes while enabled.

#### Scenario: Supported Mapper query is authorized
- **WHEN** an enabled mapped query declares its authorization resource and registered field mapping
- **THEN** the adapter applies one set-oriented constraint without per-result checks

### Requirement: Request-scoped evaluation state
Runtime integrations MUST isolate subject, request attributes, consistency context, caches, and
deadlines by request and MUST clean them after synchronous or exceptional completion.

#### Scenario: Authorized request throws an exception
- **WHEN** downstream business code fails after authorization begins
- **THEN** Forga clears all request-scoped state before the execution thread is reused

### Requirement: Integration rollback
Hosts MUST be able to disable Forga and return to their original query and authorization path
without a Forga data migration.

#### Scenario: Host rolls back integration
- **WHEN** the enable flag is turned off and the application restarts
- **THEN** Forga integration components are absent and no relationship data rollback is required
