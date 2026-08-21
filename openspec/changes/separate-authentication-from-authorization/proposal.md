## Why

Forga currently couples subject extraction to MyBatis and treats a Forga-owned Spring Web
annotation as the primary authorization entry point. Host applications need authentication-neutral
RBAC, ABAC, and ReBAC decisions, permission definitions that can be persisted and assigned, and
zero explicit authorization calls in business controllers, services, and mappers.

## What Changes

- **BREAKING** Remove `@RequiresResource`, its `NONE` marker, and the programmatic
  `ResourceAuthorizationService` business integration path.
- Add an optional `@RequiresPermission` declaration and an endpoint permission resolver SPI so
  hosts can use Forga metadata, existing host annotations, or centralized route policies.
- Require endpoint policies to resolve explicitly to a permission requirement or permit-all;
  unresolved protected entry points fail closed.
- Move authenticated-subject access into a framework-neutral contract shared by Web and MyBatis
  integrations.
- Add optional Sa-Token and Spring Security authentication adapters. They map authenticated
  principals into Forga subjects but do not perform RBAC, ABAC, or ReBAC decisions.
- Add a domain-neutral permission catalog and synchronization contract so hosts can persist stable
  permission definitions and own all role, subject, scope, and relationship assignments.
- Preserve automatic MyBatis query-constraint enforcement without requiring mapper changes.
- Do not add authentication, session, token, host persistence, management UI, or business-schema
  ownership to Forga.

## Capabilities

### New Capabilities

- `authentication-subject-adapters`: Framework-neutral authenticated-subject access with optional
  Sa-Token and Spring Security adapters.
- `endpoint-permission-resolution`: Explicit, fail-closed endpoint permission metadata resolved
  from optional Forga annotations or host-provided strategies.
- `permission-catalog`: Stable, domain-neutral permission definitions and host-owned persistence
  synchronization.

### Modified Capabilities

- None. No main specifications have been archived yet; this change supersedes the active
  `spring-web-resource-annotations` integration contract.

## Impact

- Affects `forga-core`, `forga-mybatis`, `forga-spring-web`,
  `forga-spring-boot-starter`, settings, documentation, and tests.
- Adds optional `forga-sa-token` and `forga-spring-security` modules with compile-time framework
  dependencies that do not leak into `forga-core`.
- Removes public Spring Web APIs before a stable release and requires snapshot consumers to migrate
  endpoint declarations and subject providers.
- Host applications continue to own authentication configuration, permission storage, assignments,
  relationship data, and authorization administration workflows.
