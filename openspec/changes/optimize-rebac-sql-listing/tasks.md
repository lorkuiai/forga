## 1. Query Model

- [x] 1.1 Add typed models for authorized rowset joins, projections, ordering, and list boundaries.
- [x] 1.2 Add tests for rowset model validation and no-N+1 set-based semantics.

## 2. MyBatis SQL Generation

- [x] 2.1 Extend translation to generate JOIN, SELECT projection, WHERE, and ORDER BY SQL for authorized rowsets.
- [x] 2.2 Preserve existing predicate-boundary behavior.
- [x] 2.3 Add tests for relation projection, authorization ordering, and pagination-safe SQL shape.

## 3. MyBatis Parameter Binding

- [x] 3.1 Extend bound SQL results to carry parameter values.
- [x] 3.2 Inject generated authorization parameters into MyBatis `BoundSql` additional parameters.
- [x] 3.3 Add tests for subject/scope parameter propagation and missing parameter fail-closed behavior.

## 4. Documentation And Validation

- [x] 4.1 Update README with set-based ReBAC list query guidance.
- [x] 4.2 Run `./gradlew :forga-query:test :forga-mybatis:test`.
- [x] 4.3 Run `./gradlew clean check`.
- [x] 4.4 Run `openspec validate optimize-rebac-sql-listing --strict --no-interactive`.
- [x] 4.5 Run `openspec validate add-scope-module --strict --no-interactive`.
