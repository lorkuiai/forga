# Forga

Forga is an embedded, tenant-neutral authorization engine for Java applications. It evaluates
RBAC, ReBAC, and ABAC policies over relationships and attributes supplied by the host
application.

Forga does not own business entities or require a relationship database. Applications retain
their existing permission-management and persistence model, then expose forward, reverse, and
batch relationship queries through resolver interfaces.

## Modules

- `forga-core`: authorization model, policy model, and bounded graph evaluation.
- `forga-resolver-api`: host-owned relationship and attribute resolution contracts.
- `forga-query`: typed query constraints for pushing authorization into business queries.
- `forga-mybatis`: MyBatis integration for applying query constraints without changing tables.
- `forga-spring-boot-starter`: opt-in configuration and integration lifecycle.

## Design Boundaries

- The core has no tenant, person, organization, file, or workflow concept.
- Business modules remain the source of truth for authorization relationships.
- `check`, `bulkCheck`, and `listObjects` share one policy model and bounded evaluator.
- `listObjects` uses resolver-provided reverse queries and cursor pagination; it does not loop
  over an unbounded candidate collection.
- Disabling Forga removes its authorization constraints and leaves ordinary business queries
  operational.

The initial implementation is specified in
`openspec/changes/build-embedded-authorization-engine/`.

## Build

```bash
./gradlew check
```

## Policy Example

```java
RelationRef viewer = new RelationRef("viewer");
PermissionRef view = new PermissionRef("view");

CompiledPolicy policy =
    PolicyCompiler.compile(
        new PolicyDefinition(Map.of(view, PermissionExpression.relation(viewer))),
        ResolverCapabilities.of(List.of(viewer), List.of()));
```

The names are caller-defined. Forga compares them as opaque values and does not assign built-in
meaning to any object type, subject type, relation, permission, caveat, or attribute.

## Host Resolvers

Applications expose their existing authorization data through resolver contracts. A resolver may
read from any host-owned tables or services, but it returns neutral references:

```java
RelationshipResolver resolver = ...;
ResolverRegistry registry = new ResolverRegistry(List.of(resolver));
```

Forward resolution powers `check` and `bulkCheck`. Reverse resolution powers `listObjects`.
Attribute resolution is used only for allowlisted attributes needed by caveats or query mappings.
Forga does not require a Forga-owned relationship table.

## Object Listing

`listObjects` discovers objects from reverse relationship resolver pages. It does not scan a
business object table and call `check` for each row. Objects that are not discoverable from
registered reverse relationships are outside the graph-listing boundary; hosts that need to query
their full business table should use query constraints instead.

Listing cursors are opaque and bound to request identity, policy fingerprint, consistency context,
and resolver continuation state. Reusing a cursor with a different subject, object type, or
permission fails closed.

## Query Constraints

`forga-query` represents filters as typed fields, parameters, predicates, joins, correlated
existence checks, and boolean composition. `forga-mybatis` translates only allowlisted resource
fields into parameterized SQL fragments such as `#{forga.parameters.subject}`. Unknown fields,
unsafe identifiers, or unsupported constraint nodes are rejected before SQL execution.

## Consistency And Limits

Each evaluation can carry one opaque consistency token. Resolvers may establish the token on the
first read; conflicting tokens fail closed. Evaluation and listing enforce configured depth,
visited-node, resolver-call, intermediate-result, page-size, batch-size, deadline, and cycle
bounds.

## Disabled Behavior

The starter is opt-in. When disabled, it assembles no runtime components and the MyBatis applicator
returns the original SQL unchanged, with no required authorization request context.
