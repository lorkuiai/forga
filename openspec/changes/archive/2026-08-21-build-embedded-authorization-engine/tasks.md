## 1. Core Model and Policy Validation

- [x] 1.1 Add architecture boundary tests proving `forga-core` has no Spring, MyBatis, JDBC, JPA,
  or host-package dependencies
- [x] 1.2 Implement validated object, subject, relation, permission, caveat, attribute, and
  consistency value types in `forga-core`
- [x] 1.3 Implement immutable policy expressions for relation, union, intersection, exclusion,
  subject-set traversal, and caveats
- [x] 1.4 Implement policy compilation and validation, including resolver capability checks and
  stable policy fingerprints
- [x] 1.5 Add core model and policy validation tests, then run
  `./gradlew :forga-core:test :forga-core:checkstyleMain :forga-core:checkstyleTest`

## 2. Resolver Contracts

- [x] 2.1 Define bounded forward, reverse, and batch relationship request and response contracts in
  `forga-resolver-api`
- [x] 2.2 Define attribute resolution, opaque consistency context, deadlines, cursors, and structured
  resolver failures
- [x] 2.3 Implement resolver registration and capability discovery without host-domain types
- [x] 2.4 Provide reusable resolver contract tests for batching, cursor stability, consistency, and
  forward/reverse equivalence
- [x] 2.5 Run
  `./gradlew :forga-resolver-api:test :forga-resolver-api:checkstyleMain :forga-resolver-api:checkstyleTest`

## 3. Authorization Evaluation

- [x] 3.1 Implement request-scoped breadth-wise evaluation with frontier batching and memoization
- [x] 3.2 Implement `check` and `bulkCheck` with equivalent decisions and structured proof/reason
  metadata
- [x] 3.3 Implement caveat and ABAC evaluation using allowlisted resolved and request attributes
- [x] 3.4 Enforce depth, node, resolver-call, intermediate-result, batch-size, deadline, and cycle
  bounds with fail-closed outcomes
- [x] 3.5 Add evaluation tests covering all expression types, cycles, limits, resolver failures,
  consistency conflicts, caching, and absence of N+1 resolver calls
- [x] 3.6 Run
  `./gradlew :forga-core:test :forga-core:checkstyleMain :forga-core:checkstyleTest`

## 4. Authorized Object Listing

- [x] 4.1 Implement reverse-plan compilation for union, intersection, exclusion, subject sets,
  traversal, and caveats
- [x] 4.2 Implement cursor-paginated `listObjects` using resolver reverse batches without candidate
  table scanning
- [x] 4.3 Bind opaque cursors to request identity, policy fingerprint, consistency context, and
  resolver continuation state
- [x] 4.4 Add listing tests for check/list equivalence, stable pagination, duplicates, unsupported
  reverse capabilities, limits, and bounded resolver-call counts
- [x] 4.5 Run
  `./gradlew :forga-core:test :forga-resolver-api:test :forga-core:checkstyleMain :forga-core:checkstyleTest`

## 5. Query Constraints

- [x] 5.1 Extract and neutralize typed field, parameter, predicate, join, existence, boolean, and
  correlation concepts in `forga-query`
- [x] 5.2 Implement allowlisted resource query mappings and set-oriented constraint generation
- [x] 5.3 Add tests for parameterization, boolean composition, correlation, unsupported nodes, and
  unknown field rejection
- [x] 5.4 Run
  `./gradlew :forga-query:test :forga-query:checkstyleMain :forga-query:checkstyleTest`

## 6. MyBatis and Spring Boot Integration

- [x] 6.1 Implement MyBatis translation of supported typed constraints with bound parameters and a
  single declared query interception boundary
- [x] 6.2 Implement Spring Boot conditional configuration, policy/resolver validation, request-scoped
  state cleanup, limits, and observability configuration
- [x] 6.3 Verify disabled mode registers no interceptor, requires no authorization context, and leaves
  baseline Mapper SQL unchanged
- [x] 6.4 Add integration tests for enabled failure modes, supported query translation, disabled SQL
  parity, exception cleanup, and no per-row authorization queries
- [x] 6.5 Run
  `./gradlew :forga-mybatis:test :forga-spring-boot-starter:test :forga-mybatis:checkstyleMain :forga-mybatis:checkstyleTest :forga-spring-boot-starter:checkstyleMain :forga-spring-boot-starter:checkstyleTest`

## 7. Extraction and Release Readiness

- [x] 7.1 Keep the architecture boundary tests from task 1.1 passing and verify implementation
  mechanics remain outside `forga-core` when they require framework or host-package dependencies
- [x] 7.2 Document host resolver examples, policy examples, `listObjects` discovery boundaries,
  query-constraint usage, consistency, limits, and disabled behavior
- [x] 7.3 Record that host compatibility adapters and shadow tests are outside this standalone SDK
  change and must live in host repositories
- [x] 7.4 Run `./gradlew clean check` and validate the OpenSpec change with
  `openspec validate build-embedded-authorization-engine --strict --no-interactive`
