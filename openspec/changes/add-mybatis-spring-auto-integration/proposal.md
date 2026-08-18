## Why

Forga already has neutral query constraints, MyBatis translation, and runtime assembly primitives,
but host applications still need to wire those pieces manually. A standalone SDK should provide a
generic opt-in MyBatis/Spring integration that applies authorization at declared Mapper
boundaries without introducing host-domain context.

## What Changes

- Add neutral MyBatis statement metadata describing which statement id maps to a resource and
  permission.
- Add generic subject and request-attribute provider interfaces for integrations.
- Add a MyBatis interceptor that applies at most one composed authorization constraint for
  configured statements.
- Add Spring Boot auto-configuration that conditionally registers integration beans only when
  enabled.
- Preserve disabled behavior: no interceptor registration and no required request context.
- Fail closed for missing statement metadata, subject, mappings, unsupported SQL shapes, and
  translation failures.
- Non-goals: no host-domain vocabulary, no business principal model, no permission management UI,
  and no mandatory relationship table.

## Capabilities

### New Capabilities

- `mybatis-spring-auto-integration`: Opt-in automatic MyBatis/Spring integration for applying
  neutral authorization constraints at declared Mapper statement boundaries.

### Modified Capabilities

None.

## Impact

- Adds public integration APIs in `forga-mybatis` and `forga-spring-boot-starter`.
- Adds optional MyBatis/Spring runtime classes while keeping `forga-core` framework-free.
- Requires host applications to provide policy/resolvers/resource mappings and neutral providers
  for subject and request attributes.
