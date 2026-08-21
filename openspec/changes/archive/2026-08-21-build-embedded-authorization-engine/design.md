## Context

Forga is a standalone embedded authorization SDK whose graph vocabulary is defined entirely by the
host. Existing applications already own their business data and permission-management workflows;
duplicating those relationships into an authorization database would introduce synchronization and
rollback risks.

The engine must support point checks and authorized-object discovery without N+1 queries. It also
must remain safe when policies contain cycles, deep traversals, or very broad relationships.

## Goals / Non-Goals

**Goals:**

- Provide one domain-neutral model for RBAC, ReBAC, and ABAC.
- Evaluate `check`, `bulkCheck`, and cursor-paginated `listObjects` from the same immutable policy.
- Read relationships and attributes from business-owned data through batch-capable resolvers.
- Push authorization filters into business SQL through typed, parameterized constraints.
- Keep framework adapters optional and make disabled behavior explicit.
- Bound recursion depth, visited nodes, resolver calls, intermediate sets, page size, and execution
  time.

**Non-Goals:**

- Managing permissions or business relationships.
- Requiring an authorization relationship table, external service, or distributed graph database.
- Defining tenant, person, organization, file, workflow, or other host-domain semantics.
- Replacing business transactions or guaranteeing cross-database snapshot isolation.
- Providing a textual policy language in the initial implementation; policies use a typed Java
  model that can support a parser later.

## Decisions

### Domain-neutral public boundary

Forga public APIs and core models must not expose tenant-specific or host-domain coordinates.
Host-domain names and types remain host adapter concerns and must not enter standalone Forga
modules.

`ObjectRef(type, id)` and subject references are opaque identity values. Forga may compare them,
route them to registered resolvers, include safe type names in diagnostics, and use them in stable
cursors or fingerprints. It must not parse ids, infer hierarchy, split tenant or organization
segments, normalize host identity formats, or attach built-in meaning to any reference type.

Forga may provide reusable mechanics such as typed query constraints, safe MyBatis rewriting,
fail-closed validation, disabled behavior, batching, and no-N+1 tests. It is not a source for
host-domain authorization vocabulary. Host applications must encode their own domain semantics in
resolvers, policies, query mappings, and adapters outside Forga core.

### Domain-neutral policy algebra

Core references use `ObjectRef(type, id)` and subject references use either a concrete object or a
subject set (`object#relation`). Permission expressions support relation references, union,
intersection, exclusion, relation traversal, and caveats over resolved/request attributes. The
core contains no reserved object types. A host models a hierarchy using ordinary object types and
relations in its policy.

This is preferred over preserving tenant-specific modes because it keeps the evaluator reusable
and moves business meaning into host-owned policy and adapters.

### Host-owned relationship resolution

`forga-resolver-api` defines forward, reverse, and attribute operations with batch request types.
Forward operations resolve subjects for object relations; reverse operations resolve objects for
subject relations using stable cursors; attribute operations resolve the fields explicitly
declared by a policy. Every response carries or validates a consistency token for the evaluation.

Forga will not ship a mandatory relationship store. A JDBC store can be added later as an optional
resolver for applications that choose to make Forga their relationship source of truth. This is
preferred over mandatory relation duplication because existing business permission management
remains authoritative and no dual-write protocol is needed.

### Breadth-wise batched evaluation

The evaluator compiles a permission expression into an immutable plan. For `bulkCheck`, it groups
the current graph frontier by resolver and relation, issues one batch call per group, memoizes
resolved relationships and subexpressions for the request, then advances to the next frontier.
It does not invoke a resolver once per candidate object.

Cycles are denied for the active evaluation path, while memoized completed nodes remain reusable.
Configured depth, node, resolver-call, intermediate-result, deadline, and batch-size limits fail
closed with a structured reason.

### Reverse-plan object listing

`listObjects` compiles the same permission expression in reverse. Resolver reverse operations are
the only source of candidate objects; the engine never loads an unbounded business object set and
loops over `check`. Union merges sorted streams, intersection verifies the smallest bounded stream
against the other branches in batches, exclusion subtracts in batches, and traversal walks reverse
relations breadth-wise. An opaque engine cursor contains policy fingerprint, resolver cursors, and
continuation state. Page and intermediate-set limits apply independently.

An object absent from all relationships discoverable through registered resolvers is not
discoverable through `listObjects`. This is the same graph-proof boundary used by relationship
authorization and does not imply that the business object does not exist.

### Query constraints remain a separate integration path

`forga-query` models parameterized predicates, joins, existence tests, and correlations without
embedding SQL strings. It is used when a business needs to query its own complete object table,
including objects not discoverable from relationship edges. Persistence adapters translate only
supported typed nodes and reject unknown fields or operators.

This complements rather than replaces `listObjects`: listing follows the authorization graph;
constraints filter an existing business query.

### Optional framework integration

MyBatis translates typed constraints at a declared integration point. The Spring Boot starter
assembles policies and resolvers only when enabled. Missing resolvers, invalid policies, duplicate
registrations, unsupported query nodes, and authorization failures fail closed while enabled.
When disabled, no interceptor or query constraint is installed, so original Mapper behavior is
preserved.

Dependencies point inward: Spring/MyBatis adapters depend on query and resolver APIs, while core
has no Spring, MyBatis, JDBC, or host-domain dependency.

### Consistency model

Each evaluation uses one opaque consistency context. Resolvers either honor the supplied token or
return a token on their first read that subsequent reads must accept. The default guarantee is
resolver-defined bounded consistency; strict snapshot semantics require a host resolver capable of
providing them. Mixing tokens within an evaluation fails closed.

## Risks / Trade-offs

- [Business resolvers implement incorrect reverse semantics] -> Publish resolver contract tests
  that compare `listObjects` membership with `check` over a bounded fixture.
- [Complex intersections or exclusions require large intermediate sets] -> Enforce explicit
  intermediate limits and encourage SQL query-constraint pushdown for large business listings.
- [Resolvers observe different snapshots] -> Propagate consistency context and expose a structured
  inconsistency failure instead of silently mixing results.
- [A host implements only forward lookups] -> Permit `check` but reject `listObjects` at policy
  validation when a required reverse capability is unavailable.
- [Disabled mode accidentally leaves SQL interception active] -> Build the integration as
  conditional registration and verify absence of interceptors in disabled tests.
- [SDK APIs accidentally preserve host-specific concepts] -> Add architecture tests forbidding
  host-package dependencies and framework persistence dependencies in core modules.

## Migration Plan

1. Implement and publish the standalone SDK modules.
2. Keep host-specific adapters and migration plans outside this SDK change.
3. Let host applications adopt query constraints and point checks behind their own enable flags.

Rollback for a host integration disables its SDK integration components; no Forga-owned
relationship data rollback is required.

## Open Questions

None required to begin implementation. Policy serialization and an optional JDBC relationship
resolver require separate proposals.
