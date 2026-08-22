## Why

The Spring Boot Starter enables Forga integrations but does not assemble the core
`AuthorizationEvaluator`, leaving hosts to bridge the resolver SPI to evaluator lookup contracts
and construct the engine manually. This makes the primary authorization runtime incomplete despite
explicit `@EnableForga` opt-in.

## What Changes

- Add bounded adapters from `ResolverRegistry` to `RelationshipLookup` and
  `ObjectListingLookup` without taking ownership of host relationship data.
- Auto-assemble a default `ResolverRegistry`, `EvaluationLimits`, and `AuthorizationEvaluator`
  when `@EnableForga` is present and required host policy and resolvers are available.
- Allow hosts to override lookup adapters, limits, caveat evaluation, and the evaluator Bean.
- Validate incomplete policy and resolver capabilities at startup and fail with precise errors.
- Keep endpoint-to-object mapping and `EndpointPermissionAuthorizer` host-owned because endpoint
  metadata alone cannot identify a protected object.
- Do not add environment-property enablement or evaluation tuning properties in this change.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `runtime-integration`: Automatically assemble the core authorization evaluator from host policy
  and resolver Beans after explicit annotation-based opt-in.

## Impact

The change affects `forga-spring-boot-starter` auto-configuration, adds resolver-to-evaluator
adapter APIs and tests, and updates host setup documentation. It does not change evaluator
semantics, resolver contracts, relationship persistence ownership, MyBatis query constraints, or
endpoint object mapping.
