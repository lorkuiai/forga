## Why

The standalone Starter still gates integration on the obsolete `forga.enabled` environment
property, even though the agreed host contract requires explicit source-level opt-in through
`@EnableForga`. This mismatch makes enablement environment-dependent and caused the startup banner
to remain absent from a correctly dependency-managed host.

## What Changes

- Add the public `@EnableForga` annotation for application composition roots.
- Replace property-based conditions on all Forga auto-configurations with one annotation-based
  condition.
- Make the startup banner follow the same annotation-based enablement contract.
- Update documentation, specifications, and tests to remove `forga.enabled` as an enablement path.
- Remove the redundant `ForgaIntegrationProperties` assembly model; annotation-enabled Spring
  integrations no longer require a separate enablement bean, and direct runtime assembly accepts
  `EvaluationLimits` explicitly.
- **BREAKING**: hosts using `forga.enabled=true` must annotate a configuration or application class
  with `@EnableForga`; environment properties can no longer change Forga assembly.
- **BREAKING**: direct assembler users must replace `ForgaIntegrationProperties` with an explicit
  `EvaluationLimits` argument.
- Keep authorization models, resolver ownership, persistence contracts, and disabled query behavior
  unchanged.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `runtime-integration`: Define source-level opt-in with `@EnableForga` and prohibit environment
  properties from changing assembly.
- `mybatis-spring-auto-integration`: Gate MyBatis authorization registration on `@EnableForga`
  instead of an enable flag.

## Impact

The change affects the public Spring Boot Starter API, all Starter auto-configuration conditions,
direct assembler signatures, startup banner registration, host setup documentation, and
integration tests. It does not affect `forga-core`, host relationship storage, policy evaluation
semantics, or ordinary business queries when Forga is not enabled.
