## Context

The SDK already has neutral policy evaluation, query constraints, MyBatis-safe translation, and
runtime assembly primitives. Missing is the automatic integration layer that a host can enable
without writing its own MyBatis plugin. The integration must remain generic: hosts provide
statement metadata, resource mappings, subject providers, request attributes, policies, and
resolvers.

## Goals / Non-Goals

**Goals:**

- Register a MyBatis interceptor only when Forga integration is enabled.
- Apply at most one composed typed authorization constraint per configured statement id.
- Keep subject and request attributes behind provider interfaces supplied by the host.
- Fail closed when enabled and required metadata, subject, mappings, or SQL support is missing.
- Preserve original SQL and require no request context when disabled.

**Non-Goals:**

- Defining a business principal model or host-domain context.
- Discovering Mapper semantics from business naming conventions.
- Supporting arbitrary SQL rewriting in this step.
- Adding a Forga-owned relationship store or permission management APIs.

## Decisions

### Statement Metadata Registry

The integration uses explicit metadata keyed by MyBatis `statementId`. Metadata contains only
neutral `resource`, `permission`, and a typed constraint boundary. This is preferred over
annotation scanning because it is testable without framework bootstrapping and avoids host naming
conventions.

### Provider Interfaces

The starter defines `ForgaSubjectProvider` and `ForgaRequestAttributesProvider`. They return
neutral SDK values and do not define where the host stores authentication data. Missing subject
while enabled fails closed.

### Interceptor Boundary

The MyBatis interceptor reads `MappedStatement.getId()`, resolves statement metadata, obtains the
current subject/attributes, translates the configured constraint, and appends one predicate to the
SQL. It does not call `check` per row. If the statement is not configured, the SQL is left
unchanged.

### Disabled Registration

Spring auto-configuration exposes no interceptor bean when disabled. This is stronger than a
registered no-op interceptor and preserves baseline Mapper behavior.

## Risks / Trade-offs

- [Unsupported SQL shapes] -> Restrict rewriting to simple SELECT SQL and fail closed while enabled.
- [Incorrect statement metadata] -> Require explicit registry entries and tests for missing mapping.
- [Provider missing subject] -> Fail closed before SQL execution.
- [Framework dependency leakage] -> Keep MyBatis/Spring classes in integration modules only.
