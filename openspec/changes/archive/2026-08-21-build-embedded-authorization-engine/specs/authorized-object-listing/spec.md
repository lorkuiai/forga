## ADDED Requirements

### Requirement: Relationship-backed object listing
`listObjects` MUST return objects whose permission can be proven through registered resolver data
and the same policy semantics used by `check`.

#### Scenario: Listed object passes check
- **WHEN** an object is returned by `listObjects` for a subject and permission
- **THEN** `check` for that tuple under the same consistency context returns allowed

### Requirement: Reverse traversal without candidate scanning
`listObjects` MUST begin from resolver reverse operations and MUST NOT require callers to supply or
the engine to load an unbounded collection of business objects for individual checks.

#### Scenario: Large business object table exists
- **WHEN** only a bounded subset is reachable through authorization relationships
- **THEN** listing reads bounded reverse relationship pages without scanning the business object
  table

### Requirement: Complete permission-expression semantics
Object listing MUST support union, intersection, exclusion, subject sets, traversal, and caveats,
or reject a policy at validation when a required reverse or attribute capability is unavailable.

#### Scenario: Intersection listing
- **WHEN** a permission requires membership in two relation branches
- **THEN** only objects proven by both branches are returned

### Requirement: Stable cursor pagination
Object listing MUST use opaque stable cursors bound to subject, object type, permission, policy
fingerprint, consistency context, and resolver continuation state.

#### Scenario: Cursor is reused with another permission
- **WHEN** a caller submits a cursor with request parameters different from those that created it
- **THEN** the engine rejects the cursor without returning results

#### Scenario: Final page is returned
- **WHEN** no additional authorized objects remain
- **THEN** the result has no continuation cursor and contains no duplicate object references

### Requirement: Bounded listing
Object listing MUST enforce page-size, traversal, resolver-call, intermediate-set, result, and
deadline limits.

#### Scenario: Intermediate set exceeds its limit
- **WHEN** a reverse branch produces more intermediate references than configured
- **THEN** listing stops and returns a structured limit failure rather than a partial authorization
  result
