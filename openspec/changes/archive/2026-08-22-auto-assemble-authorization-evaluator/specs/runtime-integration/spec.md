## ADDED Requirements

### Requirement: Automatic authorization evaluator assembly
When a host explicitly enables Forga, the Spring integration MUST assemble an
`AuthorizationEvaluator` from one host-owned `CompiledPolicy`, registered relationship resolvers,
and conservative default evaluation limits. Hosts MUST be able to replace the evaluator, lookup
adapters, resolver registry, caveat evaluator, and limits with their own Beans.

#### Scenario: Complete host runtime is enabled
- **WHEN** a host declares `@EnableForga`, one compiled policy, and resolvers supporting every
  relation required by that policy
- **THEN** exactly one `AuthorizationEvaluator` Bean is registered
- **AND** authorization checks use the host policy and resolver data

#### Scenario: Host overrides the evaluator
- **WHEN** an enabled host provides its own `AuthorizationEvaluator` Bean
- **THEN** the Starter backs off and does not register another evaluator

#### Scenario: Enabled runtime lacks policy or resolver capability
- **WHEN** an enabled host omits its compiled policy or a resolver capability required by that policy
- **THEN** application startup fails before requests are served with a precise configuration error

#### Scenario: Integration is not enabled
- **WHEN** the Starter is present without `@EnableForga`
- **THEN** no evaluator, registry adapter, or evaluation limits Bean is registered by Forga

### Requirement: Resolver registry evaluation bridge
The runtime integration MUST adapt forward and reverse resolver batches to evaluator lookup
contracts without per-result queries, MUST preserve reverse pagination and consistency state, and
MUST fail closed when capabilities or resolver responses are incomplete or malformed.

#### Scenario: Forward requests span resolver capabilities
- **WHEN** one evaluator lookup batch contains requests handled by multiple registered resolvers
- **THEN** requests are grouped by resolver and each resolver receives one bounded batch
- **AND** every returned direct subject and subject set is converted to an evaluator relationship
  entry

#### Scenario: Reverse resolver returns a continuation
- **WHEN** a reverse resolver returns a bounded object page with a cursor and consistency token
- **THEN** the evaluator listing page contains the same objects, cursor, and consistency token

#### Scenario: Resolver response is malformed
- **WHEN** a resolver returns a missing, duplicate, extra, null, or mismatched batch response
- **THEN** the lookup raises a structured resolver failure and authorization fails closed

### Requirement: Host-owned endpoint authorization mapping
Automatic evaluator assembly MUST NOT infer protected objects from Spring endpoint metadata. Hosts
MUST continue to provide endpoint authorization mapping when endpoint permissions are enabled.

#### Scenario: Host enables endpoint permissions
- **WHEN** endpoint permission metadata is registered
- **THEN** the host provides an `EndpointPermissionAuthorizer` that may inject the assembled
  evaluator
- **AND** the Starter does not guess an object reference from route or handler data
