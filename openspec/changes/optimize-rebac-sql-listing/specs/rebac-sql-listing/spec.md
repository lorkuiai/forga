## ADDED Requirements

### Requirement: Authorized Rowset Model

The SDK SHALL provide a typed model for joining a business resource to an authorized relationship rowset.

#### Scenario: Define authorized rowset join

- **WHEN** a host maps a resource id field to an authorization rowset object id field
- **THEN** the SDK represents the join without requiring per-row authorization checks

### Requirement: Authorization Projection

The SDK SHALL allow selected authorization rowset fields to be projected into result SQL.

#### Scenario: Project authorization relation fields

- **WHEN** a list query plan includes authorization projections
- **THEN** generated SQL includes those fields in the SELECT list with stable aliases

### Requirement: Authorization Ordering

The SDK SHALL allow ordering by authorization rowset fields and resource fields before pagination.

#### Scenario: Order by authorization rank

- **WHEN** a list query plan orders by an authorization rank field
- **THEN** generated SQL appends the authorization ordering before any database pagination is applied

### Requirement: Set-Based ReBAC SQL

The SDK SHALL generate set-based SQL for ReBAC list authorization.

#### Scenario: Generate joined authorization SQL

- **WHEN** a configured MyBatis SELECT statement uses an authorized rowset boundary
- **THEN** the rewritten SQL joins the authorization rowset and applies authorization predicates in the same SQL statement

#### Scenario: Avoid per-row checks

- **WHEN** a list statement is authorized by MyBatis
- **THEN** the interceptor MUST NOT invoke evaluator `check` for each returned row

### Requirement: MyBatis Authorization Parameters

The SDK SHALL bind generated authorization parameter values into MyBatis `BoundSql`.

#### Scenario: Bind subject and scope parameters

- **WHEN** generated SQL contains `#{forga.parameters.subject}` or scope parameters
- **THEN** the plugin adds matching MyBatis additional parameters before execution proceeds

### Requirement: Fail-Closed SQL Integration

The SDK SHALL fail closed for configured statements that cannot be safely authorized.

#### Scenario: Missing required authorization parameter

- **WHEN** a configured list statement requires a generated authorization parameter and no value is available
- **THEN** MyBatis authorization fails before executing SQL

#### Scenario: Unsupported statement shape

- **WHEN** a configured statement is not a SELECT statement
- **THEN** MyBatis authorization fails before executing SQL
