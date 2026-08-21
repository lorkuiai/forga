# Add Spring Web Resource Annotations

## Why

Host applications repeatedly need a small resource-permission guard at controller and application-service entry points. Existing business systems such as intelliconf already declare resource permissions as stable string codes like `rsc:meeting:view`, and requiring each endpoint to hand-write Forga check calls would increase migration cost and make authorization inconsistent.

## What Changes

- Add an optional Spring Web integration module that provides a resource annotation for method-level guards.
- Support a single resource-code value as the primary API so host systems can keep resource constants such as `rsc:meeting:view`.
- Provide host-owned adapters that map a resource code and optional method context to Forga `CheckRequest` or scoped check requests.
- Provide automatic Spring MVC interception for annotated handler methods.
- Provide a programmatic service for non-controller code paths that need the same resource-code check.
- Document how host systems register resource codes, current subjects, active scopes, and denial behavior.

## Non-Goals

- Do not add business resource names, tenant concepts, or intelliconf-specific enums to Forga.
- Do not own permission catalogs, role storage, membership tables, or relationship persistence.
- Do not replace host application error handling or login/session frameworks.
- Do not force host systems to split resource codes into SDK-defined resource/action enums.

## Affected Capabilities

- Spring Web integration
- Resource-code authorization
- Scope-aware host integration
