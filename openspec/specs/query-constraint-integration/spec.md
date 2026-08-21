# query-constraint-integration Specification

## Purpose
TBD - created by archiving change build-embedded-authorization-engine. Update Purpose after archive.
## Requirements
### Requirement: Typed query constraints
Forga MUST represent authorization query filters as typed fields, parameters, predicates, joins,
existence checks, boolean composition, and correlations rather than raw SQL fragments.

#### Scenario: Policy produces a correlated existence constraint
- **WHEN** a business policy requires a related membership row
- **THEN** Forga produces a typed correlated existence node with bound parameters

### Requirement: Business-owned query execution
The host persistence layer MUST remain responsible for querying business tables, and applying a
constraint MUST NOT require Forga columns or changes to business entity schemas.

#### Scenario: Forga is removed from a query
- **WHEN** the authorization constraint is not applied
- **THEN** the original business query remains structurally executable against the unchanged schema

### Requirement: Safe adapter translation
Persistence adapters MUST use allowlisted field mappings and parameter binding and MUST reject
unknown fields, operators, aliases, or unsupported constraint nodes.

#### Scenario: Unknown query field is supplied
- **WHEN** a constraint refers to a field not registered for that business query
- **THEN** translation fails closed before SQL execution

### Requirement: Set-oriented constraint generation
Constraint generation MUST produce set-oriented filters and MUST NOT require per-row authorization
queries.

#### Scenario: Business list query is authorized
- **WHEN** a host applies one generated constraint to a paginated business query
- **THEN** authorization is evaluated within the query without one resolver or SQL call per row

### Requirement: Constraint observability
Constraint generation and translation MUST expose timing, selected policy, constraint node count,
and rejection reason while omitting parameter values from logs by default.

#### Scenario: Constraint translation fails
- **WHEN** an adapter rejects a constraint node
- **THEN** a structured reason and safe metadata are observable without logging bound values

