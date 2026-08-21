## 1. Shared Authorization Context

- [x] 1.1 Add core authenticated-subject and authorization-attribute provider contracts with tests
- [x] 1.2 Migrate MyBatis integration and starter assembly to the shared contracts
- [x] 1.3 Remove the MyBatis-owned provider APIs and verify fail-closed query behavior

## 2. Permission Catalog

- [x] 2.1 Implement immutable permission definitions, contributors, and duplicate-safe catalog assembly
- [x] 2.2 Add the host-owned permission catalog synchronization contract and unit tests

## 3. Endpoint Permission Resolution

- [x] 3.1 Replace `@RequiresResource` with method/type `@RequiresPermission` and Jakarta `@PermitAll`
- [x] 3.2 Implement the endpoint resolver SPI, default resolver, and precedence/conflict tests
- [x] 3.3 Replace the resource facade with fail-closed endpoint authorizer interception and tests
- [x] 3.4 Remove legacy Spring Web resource APIs and update migration documentation

## 4. Authentication Adapters

- [x] 4.1 Add the optional `forga-sa-token` module and test authenticated and unauthenticated mapping
- [x] 4.2 Add the optional `forga-spring-security` module and test principal, anonymous, and unauthenticated mapping
- [x] 4.3 Add architecture tests proving adapter dependencies do not leak into core

## 5. Assembly And Verification

- [x] 5.1 Update Gradle settings, starter dependencies, examples, and module documentation
- [x] 5.2 Run targeted module tests and Checkstyle for every affected module
- [x] 5.3 Validate the OpenSpec change strictly and run `./gradlew clean check`
