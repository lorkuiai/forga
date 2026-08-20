# Design: Spring Web Resource Annotations

## Goals

The integration should make the common business migration path small:

```java
@RequiresResource("rsc:meeting:view")
public MeetingDetail get(String meetingId) { ... }
```

The SDK must support this shape without knowing what `meeting` means. Business systems own resource-code definitions and map those codes to policy objects, permissions, and request attributes.

## Module

Add `forga-spring-web` as an optional integration module.

Dependencies point inward:

- `forga-spring-web` depends on `forga-core` and `forga-scope`.
- It uses Spring Web MVC only for `HandlerInterceptor` integration.
- It does not depend on MyBatis, persistence, tenant packages, or a business framework.

## Public API

- `@RequiresResource`: method annotation with `String value()` and `String NONE`.
- `ResourceAuthorizationService`: programmatic `requireResource(String)` and `hasResource(String)` facade.
- `ResourceCheckAdapter`: host adapter that builds a Forga check request from a resource code and invocation context.
- `ResourceAuthorizationException`: default denial exception with stable resource code and decision.
- `RequiresResourceInterceptor`: Spring MVC `HandlerInterceptor` that finds annotations on handler methods and calls the facade.
- `ResourceInvocation`: immutable method/request context passed to the adapter.

The annotation keeps the intelliconf style as the primary API. Host applications can use constants because annotation values are compile-time strings:

```java
@RequiresResource(AdminResources.MEETING_VIEW)
```

## Authorization Flow

1. Spring MVC invokes `RequiresResourceInterceptor`.
2. The interceptor ignores non-handler methods and annotations absent or set to `NONE`.
3. It builds a `ResourceInvocation` with method metadata and optional HTTP request.
4. `ResourceAuthorizationService` calls the host `ResourceCheckAdapter`.
5. The adapter returns a Forga `CheckRequest` or `ScopedPermissionRequest` decision path by using host-owned resource catalogs, current subject providers, and active scope providers.
6. Denied decisions throw `ResourceAuthorizationException` by default.

## Host Ownership

Resource names such as `rsc:meeting:view` remain host-owned. The host adapter decides whether the code maps to:

- an application-wide object, for RBAC-like checks;
- a concrete object from method arguments, for ReBAC checks;
- active scope attributes, for cross-scope or concurrent-role checks;
- request attributes, for ABAC caveats.

## Disabled Behavior

The module is optional. If host applications do not register the interceptor, annotations have no effect. The SDK will not auto-scan or mutate controllers by default in this module.

## Performance

The interceptor performs one check per guarded entry point. It does not perform listing or data-query filtering. Collection authorization remains handled by the MyBatis/query modules so list pagination and sorting can use SQL rowsets instead of N+1 checks.
