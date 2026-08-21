## Context

`forga-core` owns authorization models, policy compilation, evaluator contracts, and low-level
relationship lookups. `forga-resolver-api` separately publishes the higher-level host resolver SPI,
batch request and response types, registry, and contract-test fixtures. Production integrations
therefore need two artifacts for one engine boundary, despite the resolver artifact having no
independent implementation or external dependency.

## Goals / Non-Goals

**Goals:**

- Publish resolver contracts and test fixtures from `forga-core`.
- Preserve existing `com.luokuiai.forga.resolver` package names and behavior.
- Remove the standalone resolver module and simplify downstream module dependencies.
- Preserve bounded batching, pagination, deadlines, and consistency contracts.

**Non-Goals:**

- Change resolver method signatures, lookup semantics, or failure behavior.
- Add storage implementations or make Forga own relationship data.
- Merge optional scope, query, MyBatis, or Spring modules into core.

## Decisions

1. Move sources without changing Java packages.

   Keeping `com.luokuiai.forga.resolver` avoids source changes for hosts. Moving classes into a new
   core package would create migration work without improving the dependency graph.

2. Publish resolver contract fixtures from core test fixtures.

   `forga-core` will apply `java-test-fixtures` and host the existing resolver contract suite. This
   preserves reusable verification for host implementations. Removing fixtures was rejected because
   it would weaken batch, cursor, and consistency conformance checks.

3. Remove the standalone project instead of leaving a forwarding artifact.

   A forwarding artifact would preserve binary dependency coordinates but retain publication and
   maintenance overhead. The project is pre-release, so consumers migrate their dependency directly
   to `forga-core` while Java imports remain stable.

4. Keep evaluation bounds and resolver semantics unchanged.

   This change only relocates APIs. Forward and reverse batch operations, bounded response sizes,
   stable cursors, deadlines, and consistency tokens continue to prevent N+1 queries and unbounded
   traversal at the resolver boundary.

## Risks / Trade-offs

- [Risk] Existing builds reference the removed artifact. -> Document replacement with
  `forga-core`; preserve all Java package names.
- [Risk] Source or test fixtures are omitted during migration. -> Move all main, test, and
  test-fixture sources and run targeted plus full Gradle checks.
- [Risk] Core becomes larger. -> Accept the additional contract types because they are fundamental
  host integration APIs and add no framework or persistence dependency.

## Migration Plan

1. Move resolver main sources and tests into `forga-core`.
2. Enable and move resolver test fixtures into `forga-core`.
3. Remove project dependencies and the standalone Gradle include.
4. Remove the empty resolver module directory and update documentation.
5. Consumers replace `forga-resolver-api` dependencies with `forga-core`; imports remain unchanged.

Rollback restores the standalone project and moves the same package sources back without API
changes.

## Open Questions

None.
