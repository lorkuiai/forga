## Why

Hosts can annotate controllers they own, but compiled SDK controllers currently require a custom
`EndpointPermissionResolver` with ad hoc method matching and separate catalog declarations. Forga
needs a startup-validated external registration path that is as safe and consistent as
`@RequiresPermission`.

## What Changes

- Add a Spring Web endpoint permission registry and contributor API for exact controller method
  signatures.
- Support required-permission and explicit permit-all registrations for controllers that hosts
  cannot modify.
- Compile registrations against actual Spring MVC handler methods at startup and reject missing,
  ambiguous, duplicate, or annotation-conflicting declarations.
- Contribute permission definitions declared by endpoint registrations to the ordinary permission
  catalog without making the core catalog depend on Spring Web.
- Add Spring Boot auto-configuration that assembles endpoint metadata and registers enforcement once.
- Preserve `@RequiresPermission`, Jakarta `@PermitAll`, the resolver SPI, and fail-closed behavior for
  unresolved handlers.
- Keep URL-pattern authorization, SDK business models, and host persistence outside this change.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `endpoint-permission-resolution`: Add exact external handler registration, startup validation,
  deterministic composition with annotations, and automatic MVC enforcement assembly.
- `permission-catalog`: Allow Spring Web endpoint contributors to supply ordinary permission
  definitions while preserving catalog independence from endpoint discovery.

## Impact

This changes public APIs in `forga-spring-web`, adds optional MVC auto-configuration in
`forga-spring-boot-starter`, and extends tests and documentation. Host applications retain ownership
of permission assignment and persistence; existing annotation and resolver integrations remain
source compatible.
