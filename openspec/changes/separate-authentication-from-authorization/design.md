## Context

The current MyBatis module owns `ForgaSubjectProvider` and request-attribute access, while the
Spring Web module exposes `@RequiresResource` plus a programmatic authorization facade. This makes
identity framework adaptation a persistence concern and encourages authorization calls in business
services. Hosts also need a stable permission inventory that can be synchronized into host-owned
storage before administrators assign permissions.

Forga remains the policy decision point for RBAC, ABAC, and ReBAC. Sa-Token or Spring Security may
authenticate requests, but their role/authority APIs are not authorization inputs. Host resolvers
remain responsible for permission assignments, relationships, and attribute persistence.

## Goals / Non-Goals

**Goals:**

- Give Web and MyBatis integrations one neutral authenticated-subject contract.
- Keep endpoint authorization declarative and automatic, with no explicit Forga call in business
  controllers, services, or mappers.
- Let hosts use a Forga annotation, an existing host annotation, or centralized route metadata.
- Fail closed when a handler has neither a permission requirement nor an explicit permit-all rule.
- Provide optional Sa-Token and Spring Security subject adapters without authorization logic.
- Publish stable permission definitions through a host-owned synchronization contract.

**Non-Goals:**

- Authentication, token issuance, sessions, OAuth, or login flows.
- Forga-owned permission, role, assignment, relationship, or business data tables.
- Loading Sa-Token permission lists or Spring Security granted authorities as final decisions.
- Filtering result collections in memory or replacing MyBatis query constraints.
- Automatically interpreting arbitrary controller parameters or request bodies as object ids.

## Decisions

### Shared authentication contract belongs in core

Add `AuthenticatedSubjectProvider` under a neutral core context package. It returns an optional
`SubjectRef`; empty means no authenticated subject. Move request attributes to a companion
`AuthorizationAttributesProvider`. MyBatis consumes these contracts instead of owning them.

Alternative: keep framework providers in MyBatis. Rejected because Web authorization and future
non-persistence integrations require the same subject independently of SQL.

### Authentication adapters expose identity only

`forga-sa-token` accepts an injectable `StpLogic` and maps its login id to a caller-configured
subject type. `forga-spring-security` maps an authenticated, non-anonymous `Authentication` to a
subject. Both modules depend toward core and expose no permission-check methods.

Alternative: implement Sa-Token `StpInterface` or consume Spring `GrantedAuthority`. Rejected
because flat framework authorities cannot express request attributes, object relationships, or
bounded graph traversal and would create two authorization sources of truth.

### Endpoint metadata is resolved through an SPI

`EndpointPermissionResolver` resolves a handler and request to either a required `PermissionRef`,
explicit permit-all, or unresolved. The default resolver supports method/class
`@RequiresPermission` and Jakarta `@PermitAll`; method metadata takes precedence. An unresolved
handler is denied before invocation. Hosts can replace or compose resolvers for existing business
annotations or centralized routes.

The interceptor delegates resolved requirements to an `EndpointPermissionAuthorizer` implemented
by host integration configuration. Business methods never call that authorizer directly.

Alternative: keep `ResourceAuthorizationService` public for manual calls. Rejected because it
makes authorization optional at each call site and couples services to Forga.

### Permission definitions are code-owned, persistence is host-owned

A `PermissionCatalog` contains immutable `PermissionDefinition` entries keyed by `PermissionRef`.
Contributors allow independent host modules to provide definitions. A
`PermissionCatalogSynchronizer` receives the assembled catalog and persists it using host rules.
Forga validates duplicates but never deletes, deprecates, or stores definitions itself. The Spring
Boot starter automatically invokes one host synchronizer after singleton assembly when configured.

Alternative: discover permissions only by scanning annotations. Rejected because permissions can
serve non-Web entry points and unused-but-assignable capabilities; usage sites are not a complete
catalog.

### Query authorization remains database-side

The MyBatis interceptor keeps statement-level query constraints and obtains identity from the
shared providers. No adapter performs per-row checks, so filtering, sorting, and pagination remain
inside one bounded SQL query. Existing configured SELECT fail-closed behavior remains unchanged.

## Risks / Trade-offs

- [Breaking snapshot API removal] -> Remove the old types directly before a stable release and
  document exact replacements.
- [Both authentication adapters are active] -> Discover providers without a selector property and
  fail startup unless exactly one `AuthenticatedSubjectProvider` bean exists.
- [Endpoint metadata is omitted] -> Default deny at runtime and provide resolver coverage tests.
- [Route metadata drifts] -> Resolve against Spring `HandlerMethod` and allow startup validation in
  host integrations instead of trusting URL strings alone.
- [Catalog synchronization damages assignments] -> The synchronizer is host-owned; Forga only
  supplies an immutable snapshot and never prescribes deletion behavior.
- [Authentication APIs change] -> Keep framework dependencies compile-only and cover adapters with
  focused compatibility tests.

## Migration Plan

1. Replace `ForgaSubjectProvider` and `ForgaRequestAttributesProvider` implementations with the
   shared core contracts.
2. Replace `@RequiresResource` with `@RequiresPermission`, or supply a host resolver for existing
   annotations. Replace `NONE` with Jakarta `@PermitAll`.
3. Add exactly one authentication adapter or host subject provider; no adapter selector is needed.
4. Register permission contributors and an optional host catalog synchronizer.
5. Remove business calls to `ResourceAuthorizationService`; endpoint interceptors enforce policy.

Rollback requires returning to the preceding snapshot because removed public APIs are not retained.

## Open Questions

- Non-HTTP entry-point interceptors will use the same subject contract in later optional modules.
