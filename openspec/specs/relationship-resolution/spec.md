# relationship-resolution Specification

## Purpose
TBD - created by archiving change build-embedded-authorization-engine. Update Purpose after archive.
## Requirements
### Requirement: Business-owned relationship source
The resolver API MUST allow hosts to expose existing relationship and attribute data without
copying it into a Forga-owned table or modifying business entity schemas.

#### Scenario: Existing relationship table is adapted
- **WHEN** a host registers a resolver backed by its existing permission data
- **THEN** the engine consumes neutral references while the host remains responsible for all data
  mutation and persistence

### Requirement: Forward, reverse, and batch resolution
Resolvers MUST declare and implement supported forward, reverse, batch, and attribute capabilities,
and every collection operation MUST be bounded.

#### Scenario: Batch frontier is resolved
- **WHEN** the evaluator requests the same relation for multiple objects
- **THEN** the resolver receives a single bounded batch request and returns results keyed by request

#### Scenario: Unsupported reverse operation
- **WHEN** a policy used by `listObjects` requires reverse resolution not declared by its resolver
- **THEN** policy validation rejects that listing operation before traversal starts

### Requirement: Resolver consistency context
Resolver calls within one evaluation MUST propagate a common opaque consistency context, and the
engine MUST reject conflicting tokens.

#### Scenario: Resolver establishes a token
- **WHEN** the first resolver read establishes a consistency token
- **THEN** every subsequent read in that evaluation receives the same token

### Requirement: Resolver failure isolation
The engine MUST fail closed with a structured error when a resolver times out, returns malformed or
conflicting data, or reports an unavailable dependency.

#### Scenario: Resolver times out
- **WHEN** a resolver exceeds the remaining evaluation deadline
- **THEN** the engine stops dependent evaluation and returns a resolver-timeout failure

### Requirement: Resolver contract verification
The project MUST provide reusable contract tests for ordering, cursor stability, batching,
consistency propagation, and forward/reverse equivalence.

#### Scenario: Host validates a resolver
- **WHEN** a host runs the resolver contract suite against a fixture
- **THEN** the suite verifies that each reverse result is accepted by the corresponding forward
  relationship query

