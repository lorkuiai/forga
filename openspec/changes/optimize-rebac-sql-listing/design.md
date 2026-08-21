## Context

Forga already has typed query constraints and MyBatis SQL predicate translation. That is enough for simple `WHERE EXISTS` authorization, but ReBAC list pages often need the authorization relationship rows themselves: the relation type, scope, rank, source, or assignment status can be displayed and used for ordering before pagination.

## Goals / Non-Goals

**Goals:**

- Model authorized relationship rowsets as typed query inputs.
- Join business resources to an authorized rowset in one SQL statement.
- Project authorization rowset fields into the SQL result.
- Order by authorization rowset fields and resource fields before pagination.
- Bind generated authorization parameters into MyBatis `BoundSql`.
- Preserve fail-closed behavior for configured statements with missing subject or unsafe SQL.

**Non-Goals:**

- Compile arbitrary recursive graph traversal into SQL.
- Own or prescribe a relationship table schema.
- Execute per-row `check` calls from MyBatis interceptors.

## Decisions

1. Add an authorized rowset plan separate from `QueryConstraint`.

   `QueryConstraint` remains a predicate tree. ReBAC list optimization needs a richer shape that describes joins, selected authorization columns, ordering, and parameters. This avoids overloading predicate constraints with SELECT/JOIN behavior.

2. Use allowlisted resource mappings for both business resources and authorization rowsets.

   All table and column references continue to flow through `MyBatisResourceMapping`. Host applications can map a table, view, CTE-compatible view, or materialized relation projection without Forga owning storage.

3. Generate set-based SQL only.

   The MyBatis path emits one SQL statement with JOIN/WHERE/ORDER BY fragments. It does not call `check` per row, so filtering and ordering happen before database pagination.

4. Bind parameters through `BoundSql` additional parameters.

   Generated SQL uses `#{forga.parameters.<name>}` placeholders. The plugin writes the provided authorization parameter values into MyBatis additional parameters so the rewritten SQL can execute without mutating host parameter objects.

## Risks / Trade-offs

- [Risk] SQL rewriting remains conservative and does not parse every SQL dialect. -> Mitigation: only configured SELECT statements are rewritten, and unsafe shapes fail closed.
- [Risk] Hosts can define inefficient authorization rowsets. -> Mitigation: SDK docs and APIs model set-based joins, but index design remains host-owned.
- [Risk] Duplicate rows can appear when the authorization rowset has multiple matching rows. -> Mitigation: hosts should map rowsets/views at the cardinality required by the list; the SDK exposes projection and ordering but does not hide row multiplicity.
