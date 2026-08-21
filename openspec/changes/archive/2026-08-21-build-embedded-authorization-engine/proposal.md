## Why

Forga is being developed as a standalone, domain-neutral authorization SDK. It creates one
authorization model for RBAC, ReBAC, and ABAC while leaving host applications in control of their
own data and permission management.

## What Changes

- Create a standalone Java 17 Gradle project with isolated core, resolver, query, MyBatis, and
  Spring Boot integration modules.
- Introduce a tenant-neutral object, subject, relation, permission, caveat, and attribute model.
- Provide bounded `check`, `bulkCheck`, and cursor-paginated `listObjects` APIs over one policy
  evaluator.
- Define forward, reverse, and batch resolver contracts so host applications remain the source of
  truth for relationships and attributes.
- Provide typed query constraints that integrations can push into business-owned SQL without
  changing business tables.
- Make framework integration opt-in and define disabled behavior that leaves ordinary business
  queries unchanged.
- Keep host-specific policy, synchronization, and business adapters outside the SDK.
- Treat an optional Forga-owned relationship store as a future adapter, not a core requirement.
- Non-goals: Forga will not manage business permissions, duplicate business entities or
  relationships, prescribe tenant semantics, or expose a required network service.

## Capabilities

### New Capabilities

- `authorization-evaluation`: Domain-neutral RBAC, ReBAC, and ABAC policy evaluation with bounded
  graph traversal and consistent single and batch checks.
- `relationship-resolution`: Host-owned forward, reverse, batch, attribute, and consistency-aware
  resolver contracts.
- `authorized-object-listing`: Cursor-paginated object discovery through reverse relationship
  resolution without unbounded candidate iteration.
- `query-constraint-integration`: Typed authorization constraints suitable for safe pushdown into
  business persistence queries.
- `runtime-integration`: Optional MyBatis and Spring Boot integration, including explicit disabled
  behavior and fail-closed configuration validation.

### Modified Capabilities

None. This is a new standalone project.

## Impact

- Adds the public modules `forga-core`, `forga-resolver-api`, `forga-query`, `forga-mybatis`, and
  `forga-spring-boot-starter`.
- Establishes new Java APIs for policies, resolvers, checks, object listing, query constraints, and
  runtime configuration.
- Host applications may build adapters over their own permission data outside this SDK.
- Adds no mandatory database, relation table, remote service, or business schema migration.
