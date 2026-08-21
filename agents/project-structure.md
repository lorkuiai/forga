## Project Structure & Module Organization

- `openspec/`: Spec-driven development artifacts.
  - `config.yaml`: OpenSpec configuration.
  - `changes/`: active proposals, design docs, delta specs, and tasks.
  - `specs/`: archived or synced capability specs when present.
- `forga-core/`: domain-neutral authorization model, policy model, bounded evaluator, and host
  resolver contracts.
- `forga-query/`: typed query constraint model for pushing authorization into host queries.
- `forga-mybatis/`: optional MyBatis integration for translating and applying query constraints.
- `forga-spring-boot-starter/`: optional Spring Boot assembly and integration lifecycle.
- `forga-scope/`: optional scope switching, acting context, and active-scope query helpers.
- `gradle/`: shared Gradle configuration such as Checkstyle rules.

## Ownership Boundaries

- Forga modules provide SDK contracts, policy/evaluation primitives, and optional integrations.
- Host applications own business data, relationship storage, persistence schemas, and business names.
- Core authorization APIs compare caller-defined opaque references and do not assign business meaning to object, subject, relation, permission, caveat, attribute, or scope names.
- Optional modules may provide reusable patterns, but they must not force a host application storage model.
