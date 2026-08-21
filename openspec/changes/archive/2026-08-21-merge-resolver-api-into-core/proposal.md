## Why

Resolver contracts are required by production Forga integrations but are published in a separate
artifact from the core evaluator, while core already owns the lower-level lookup boundary. Merging
the contracts into core removes an unnecessary artifact and presents one coherent authorization
engine API to host applications.

## What Changes

- Move resolver contracts, value types, registry, tests, and contract-test fixtures into
  `forga-core` without changing their Java package names or runtime behavior.
- Update all modules and documentation to consume resolver APIs from `forga-core`.
- **BREAKING**: Stop publishing and remove the `forga-resolver-api` Gradle/Maven artifact; consumers
  that declare it directly must replace that dependency with `forga-core`.
- Keep relationship data, persistence, and resolver implementations owned by host applications.

## Capabilities

### New Capabilities

- `core-resolver-contracts`: Resolver SPI packaging, compatibility, and contract-test fixture
  requirements within the core artifact.

### Modified Capabilities

None.

## Impact

- Affected modules: `forga-core`, `forga-query`, `forga-spring-boot-starter`, and project build
  configuration.
- Affected documentation: module inventory, dependency guidance, and architecture constraints.
- Host source compatibility is preserved for `com.luokuiai.forga.resolver` imports.
- Host build files that depend directly on `com.luokuiai.forga:forga-resolver-api` must migrate to
  `com.luokuiai.forga:forga-core`.
