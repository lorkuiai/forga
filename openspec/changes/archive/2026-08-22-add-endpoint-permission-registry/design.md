## Context

`forga-spring-web` currently supports annotations and a low-level resolver SPI. A host integrating a
compiled SDK controller must implement method matching, catalog contribution, resolver composition,
and MVC interceptor registration itself. String method-name or URL matching is brittle, and the
current assembly cannot validate missing or conflicting declarations at startup.

## Goals / Non-Goals

**Goals:**

- Provide exact, external permission declarations for unmodifiable Spring MVC controllers.
- Use one declaration to produce endpoint metadata and ordinary catalog definitions.
- Validate controller signatures, handler coverage, duplicate definitions, and annotation conflicts
  before serving requests.
- Preserve annotation, permit-all, custom resolver, and fail-closed behavior.
- Automatically install the common interceptor for registry-based integrations.

**Non-Goals:**

- Add URL-pattern authorization or a second authorization engine.
- Put Spring types, controller metadata, or route discovery into `forga-core`.
- Own host role assignment, persistence, or SDK business concepts.
- Add WebFlux, Servlet, or filter interception in this change.

## Decisions

### Register exact controller method references

`ControllerMethodRef` identifies a controller class, method name, and exact parameter types.
Contributors register required permissions or explicit permit-all metadata through an
`EndpointPermissionRegistry`. Exact signatures make overloads deterministic and allow missing
methods to fail during startup. URL matching was rejected because composed mappings, context paths,
and SDK route changes make it less stable than handler identity.

### Compile registrations against Spring handler mappings

An immutable registrations object is assembled from all contributors. After MVC handler discovery,
the Starter matches each reference to exactly one `HandlerMethod`, rejects non-handler methods, and
builds a method-keyed lookup used at request time. Runtime resolution is bounded map access and does
not scan controllers or issue data queries.

### Treat annotations and registration as equal metadata sources

The compiled resolver evaluates default annotations, external registrations, and custom resolver
SPI results. Identical results are deduplicated; required-permission versus permit-all or different
permissions are conflicts. Static annotation and registry conflicts fail at startup, while conflicts
from request-dependent custom resolvers fail closed at request time.

### Keep catalog domain-neutral through an adapter contributor

A required endpoint registration carries a `PermissionDefinition`. The Starter exposes the unique
definitions as one ordinary `PermissionCatalogContributor` before catalog assembly. Multiple
endpoints may reuse an identical definition; conflicting metadata for one `PermissionRef` is
rejected. `forga-core` remains unaware of controllers and endpoint discovery.

### Auto-configure registry-based MVC enforcement

When Forga is enabled and an `EndpointPermissionContributor` plus `EndpointPermissionAuthorizer`
are present, the Starter creates and registers one interceptor. Existing applications without
endpoint contributors retain their current manual integration. A host-provided interceptor bean
causes the auto-configured interceptor to back off.

## Risks / Trade-offs

- [SDK method signature changes break startup] -> Report the unresolved controller signature so the
  host updates its registration before serving traffic.
- [A newly added SDK endpoint is not registered] -> It remains unresolved and fails closed; optional
  complete-controller coverage validation can be added separately.
- [A host manually registers the same interceptor and then adds contributors] -> Back off when an
  interceptor bean is present and document removal of manual MVC registration.
- [Custom request-dependent resolvers cannot be fully validated at startup] -> Compare all resolved
  results per request and fail closed on disagreement.
- [One permission protects several endpoints] -> Deduplicate identical definitions while retaining
  separate endpoint bindings.

## Migration Plan

Existing annotation-only and custom resolver integrations continue unchanged. SDK integrations add
an `EndpointPermissionContributor`, remove duplicate manual catalog entries, and rely on Starter MVC
registration when they do not already expose an interceptor bean. Rollback removes the contributor
and restores the prior resolver and MVC configuration.

## Open Questions

None.
