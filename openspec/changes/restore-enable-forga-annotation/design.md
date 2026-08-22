## Context

Forga is opt-in, but the standalone Starter currently uses `forga.enabled` on each
auto-configuration. The final integration contract uses a source-level marker on the host
composition root so deployment configuration cannot silently enable or disable authorization.
All Starter surfaces, including authentication validation, catalog assembly, Spring Web, MyBatis,
and the startup banner, must observe one enablement signal.

## Goals / Non-Goals

**Goals:**

- Provide a documented public `@EnableForga` marker for host configuration classes.
- Make every Forga auto-configuration conditional on that marker.
- Ensure `forga.enabled` and equivalent environment values have no assembly effect.
- Preserve fail-fast validation after annotation-based enablement.
- Keep disabled hosts free of Forga interceptors, request state, and SQL changes.

**Non-Goals:**

- Change policy evaluation, resolver contracts, query translation, or relationship ownership.
- Add component scanning or import host-specific configuration.
- Remove the programmatic `ForgaIntegrationProperties` assembly model in this change.
- Change collection evaluation, pagination, or traversal limits.

## Decisions

### Use a marker annotation instead of configuration properties

`@EnableForga` is retained at runtime and targets types. Hosts place it on the Spring Boot
application class or another configuration class. This makes authorization enablement visible in
source review and prevents an environment variable from changing the security architecture.

The alternative of keeping both property and annotation enablement is rejected because it creates
two authorities and lets configuration override the explicit composition-root decision.

### Centralize the Spring condition

An internal `@ConditionalOnForgaEnabled` composed condition uses
`@ConditionalOnBean(annotation = EnableForga.class)`. Every Forga auto-configuration uses that
condition, including the banner. The marker does not import configurations itself; Spring Boot's
standard auto-configuration discovery remains responsible for loading the optional integration.

The alternative of repeating a custom condition on each class is rejected because enablement
semantics could drift again.

### Keep programmatic assembly separate

`ForgaIntegrationProperties` remains available to direct assembler users. Its `enabled` value does
not activate Spring Boot auto-configuration; only `@EnableForga` does. If annotation-enabled
assembly receives incomplete or internally disabled runtime inputs, existing fail-fast behavior is
preserved rather than silently degrading.

### Preserve persistence and evaluation behavior

The change only selects whether integration beans exist. Once enabled, existing typed query
constraints, database pagination, resolver batching, and bounded evaluation remain unchanged, so it
does not introduce N+1 access or unbounded traversal.

## Risks / Trade-offs

- **Existing hosts rely on `forga.enabled=true`** -> Document the breaking migration to
  `@EnableForga` and test that the old property has no effect.
- **One auto-configuration misses the composed condition** -> Search all Starter
  auto-configurations and cover absence/presence through aggregate enablement tests.
- **Marker detection depends on configuration registration order** -> Use Spring Boot's
  `@ConditionalOnBean(annotation = ...)` mechanism and verify it with `ApplicationContextRunner`
  user configurations.
- **Disabled hosts accidentally retain enforcement** -> Test both missing annotation and legacy
  property-only startup for absence of Forga beans and banner registration.

## Migration Plan

1. Add `@EnableForga` to the host Spring Boot application or configuration class.
2. Remove `forga.enabled` from application files and deployment environment variables.
3. Upgrade to the corrected Forga Snapshot and restart.
4. Verify the versioned Forga banner and required infrastructure validation at startup.

Rollback requires removing `@EnableForga` and returning to the previous artifact version; no data
migration is involved.

## Open Questions

None.
