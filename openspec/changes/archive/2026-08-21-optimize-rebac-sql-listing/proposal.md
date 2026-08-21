## Why

ReBAC list queries must be authorized before sorting and pagination without per-row checks. Current MyBatis support can append predicates, but it does not model an authorized relation rowset that can be joined, projected, sorted, and parameterized for production list pages.

## What Changes

- Add a typed authorized rowset model for set-based ReBAC list authorization.
- Support joining host resources to an authorized relationship rowset before sorting and pagination.
- Support projecting authorization relationship fields into the result SQL.
- Support ordering by authorization relationship fields and ordinary resource fields.
- Complete MyBatis parameter binding for generated authorization parameters.
- Keep single-resource ReBAC checks on the evaluator/resolver path; SQL support is for set-based list queries.
- Non-goal: implement a recursive graph compiler inside the MyBatis interceptor.
- Non-goal: require a Forga-owned relationship table.

## Capabilities

### New Capabilities

- `rebac-sql-listing`: Set-based SQL listing support for ReBAC relationship rowsets, authorization projections, ordering, pagination-safe joins, and parameter binding.

### Modified Capabilities

- None.

## Impact

- Extends `forga-query` with authorized rowset, projection, and ordering models.
- Extends `forga-mybatis` with SQL translation/application for authorized rowset joins.
- Completes MyBatis parameter propagation for generated authorization parameters.
- Adds tests covering no N+1 behavior, SQL shape, authorization projections, ordering, and parameter binding.
