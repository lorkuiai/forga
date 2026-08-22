## 1. Resolver Evaluation Bridge

- [x] 1.1 Implement the forward `ResolverRegistryRelationshipLookup` adapter with bounded grouped
  batches and strict response validation.
- [x] 1.2 Implement the reverse `ResolverRegistryObjectListingLookup` adapter with cursor and
  consistency conversion.
- [x] 1.3 Add core tests for direct subjects, subject sets, multi-resolver batching, reverse pages,
  missing capabilities, and malformed responses.

## 2. Spring Runtime Assembly

- [x] 2.1 Add annotation-gated auto-configuration for `ResolverRegistry`, lookup adapters,
  `EvaluationLimits`, and `AuthorizationEvaluator`.
- [x] 2.2 Add auto-configuration discovery/order metadata and tests for enabled, disabled, incomplete,
  caveat-aware, and host override scenarios.
- [x] 2.3 Document the host policy/resolver contract and evaluator injection path without adding
  environment properties.

## 3. Verification

- [x] 3.1 Run core and Starter tests plus Checkstyle for affected modules.
- [x] 3.2 Run full `clean check` and strict OpenSpec validation.
