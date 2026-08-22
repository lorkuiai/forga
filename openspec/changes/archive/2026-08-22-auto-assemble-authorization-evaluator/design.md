## Context

`AuthorizationEvaluator` consumes the compact `RelationshipLookup` and `ObjectListingLookup`
contracts, while host persistence integrations implement the richer `RelationshipResolver` SPI and
register through `ResolverRegistry`. No production adapter currently connects these APIs, and the
Spring Boot Starter does not create an evaluator. Hosts therefore have to reproduce SDK assembly
logic even after opting in with `@EnableForga`.

The resolver SPI and evaluator both live in `forga-core`; Spring assembly remains in
`forga-spring-boot-starter`. Host applications continue to own relationship storage, policy
definitions, endpoint-to-object mapping, and caveat semantics.

## Goals / Non-Goals

**Goals:**

- Provide reusable forward and reverse adapters from `ResolverRegistry` to evaluator lookups.
- Route a lookup batch to each capable resolver once and preserve reverse cursor and consistency
  state.
- Fail closed for missing resolver capabilities, exceptions, incomplete batches, duplicate
  responses, and responses that do not match a request.
- Auto-configure the registry, lookup adapters, conservative limits, and evaluator after
  `@EnableForga` opt-in.
- Let hosts replace every default Bean through `@ConditionalOnMissingBean`.

**Non-Goals:**

- Own or infer host relationship persistence or business object meanings.
- Auto-create `CompiledPolicy`; policy construction remains host-owned.
- Auto-create `EndpointPermissionAuthorizer`; mapping an endpoint invocation to an `ObjectRef`
  requires host semantics not present in endpoint metadata.
- Add `application.yml` enablement or evaluation tuning.
- Change resolver bounds, evaluator traversal behavior, or MyBatis constraint translation.

## Decisions

### Put resolver adapters in core

Add separate `ResolverRegistryRelationshipLookup` and `ResolverRegistryObjectListingLookup`
implementations in `forga-core`. Both sides of the adaptation are domain-neutral core APIs, and
non-Spring hosts need the same bridge. Separate classes also let Spring hosts override forward and
reverse lookup independently without creating ambiguous Beans from one object implementing both
interfaces.

The alternative of package-private Starter adapters is rejected because it duplicates generic
authorization mechanics and prevents direct assembler users from reusing them.

### Route and validate bounded batches

Each adapter deduplicates input requests, selects a resolver by declared capability, groups requests
by resolver, and invokes one resolver batch per group. Resolver responses must match the submitted
requests exactly. Missing, duplicate, extra, null, or mismatched results raise
`RelationshipLookupException` with `RESOLVER_FAILURE` so evaluation denies access.

Resolver request page sizes are capped at the resolver SPI maximum of 1,000. Reverse continuation
cursors are returned to the evaluator, so subsequent listing requests continue without unbounded
candidate loading. The existing forward evaluator contract has no cursor or consistency output;
the adapter therefore retains its existing bounded, fail-closed semantics but cannot propagate a
forward resolver consistency token.

### Assemble overridable singleton engine dependencies

Add `ForgaEvaluatorAutoConfiguration`, gated by `@EnableForga`. It creates these defaults only when
missing:

- `ResolverRegistry` from all host `RelationshipResolver` Beans
- `RelationshipLookup` and `ObjectListingLookup` from the registry adapters
- `EvaluationLimits.defaults()`
- `AuthorizationEvaluator` from host `CompiledPolicy`, the lookup Beans, limits, and an optional
  `CaveatEvaluator`

The evaluator is safe as a singleton because evaluation state, caches, counters, proofs, and
consistency state are allocated per `check`, `bulkCheck`, or `listObjects` call. Hosts can replace
the evaluator or any dependency with their own Bean.

### Keep Web authorization mapping host-owned

The Starter does not derive an `EndpointPermissionAuthorizer` from the evaluator. An
`EndpointInvocation` identifies a permission and HTTP handler but not the protected `ObjectRef`, so
generic assembly would require guessing host routing semantics. A host authorizer can inject the
auto-configured evaluator and construct the appropriate `CheckRequest`.

## Risks / Trade-offs

- **Malformed resolver batches could silently omit access relationships** -> Validate exact request
  and response correspondence and fail closed.
- **Multiple resolvers claim the same relation** -> Preserve current registry selection semantics;
  startup capability validation confirms availability but does not introduce a new precedence
  model.
- **Forward consistency cannot be propagated by `RelationshipLookup`** -> Keep bounded fail-closed
  behavior and document the existing contract limitation; do not invent a lossy consistency API.
- **A fixed absolute evaluator deadline is unsafe for a singleton** -> Default to
  `EvaluationLimits.defaults()` with no deadline; hosts using deadlines must provide appropriate
  request-specific evaluator construction until timeout semantics are redesigned.

## Migration Plan

1. Host applications keep `@EnableForga` and provide one `CompiledPolicy` plus their
   `RelationshipResolver` Beans.
2. Remove manually duplicated registry lookup adapters and evaluator construction, or retain them as
   overriding Beans.
3. Inject `AuthorizationEvaluator` into host authorization adapters and services.
4. Roll back by removing the new Starter version or providing a host-owned evaluator Bean; no data
   migration is required.

## Open Questions

None.
