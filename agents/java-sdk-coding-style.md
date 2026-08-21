## Forga Java SDK Coding Style

### Java

- Java 17 is required.
- Google Java formatting conventions are required.
- Line width is 100 chars, indent is 2 spaces, and wildcard imports are forbidden.
- `@Override` must be present where applicable.
- Empty catch blocks are forbidden.
- Prefer immutable types and records for value objects, requests, and responses.
- Do not add Lombok to SDK modules.

### Public API

- Public APIs require Javadocs.
- Public methods with inputs or return values require `@param` and `@return` tags where applicable.
- API names must be stable, searchable, and domain-neutral.
- Constructor and record validation must fail early with clear messages matching local style.
- Avoid introducing synonyms for existing concepts such as object, subject, relation, permission, caveat, attribute, resolver, policy, scope, and query constraint.

### Authorization Model

- `forga-core` must stay domain-neutral.
- Core APIs must not contain host business concepts or persistence assumptions.
- Host applications own business data, authorization relationships, and storage schemas.
- Relationship storage integrations must implement resolver contracts instead of adding storage ownership to core APIs.
- Authorization failure paths must fail closed and preserve stable `DecisionReason` values where possible.

### Module Boundaries

- Lower-level modules must not depend on optional framework integrations.
- `forga-core` owns model, policy, evaluator, limits, and decisions.
- `forga-resolver-api` owns host resolver contracts and fixtures.
- `forga-query` owns typed query constraint structures.
- `forga-mybatis` owns MyBatis SQL translation/application.
- `forga-spring-boot-starter` owns opt-in Spring assembly.
- `forga-scope` owns optional scope switching and active-scope helpers.

### Query And Resolver Rules

- Query helpers must produce typed, parameterized constraints.
- SQL translators must only use allowlisted fields and safe identifiers.
- Do not build SQL by concatenating untrusted identifiers or values.
- Collection authorization APIs must avoid unbounded per-row checks.
- Prefer resolver batch APIs, reverse lookup, pagination, evaluator bounds, or query constraints.

### Tests

- Public API behavior requires unit tests.
- Tests should cover allowed, denied, invalid input, and fail-closed paths.
- Boundary tests should check architecture and dependency constraints, not business vocabulary.
- Ordinary unit tests must not require external infrastructure.

### Comments

- Comments and Javadocs should explain useful intent, constraints, or API contracts.
- Do not add comments that merely restate the code.
- Keep comments ASCII unless the file already uses another character set for a clear reason.
