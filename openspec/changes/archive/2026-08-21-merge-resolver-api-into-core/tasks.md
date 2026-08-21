## 1. Core Migration

- [x] 1.1 Enable core test fixtures and move resolver main APIs into `forga-core`.
- [x] 1.2 Move resolver unit tests and contract-test fixtures into `forga-core`.
- [x] 1.3 Run core tests and Checkstyle for migrated resolver APIs.

## 2. Module Removal

- [x] 2.1 Replace project dependencies on `forga-resolver-api` with `forga-core`.
- [x] 2.2 Remove the resolver project from Gradle settings and delete its module directory.
- [x] 2.3 Update architecture tests, module documentation, and development instructions.

## 3. Verification

- [x] 3.1 Validate the OpenSpec change strictly.
- [x] 3.2 Run `./gradlew clean check` successfully.
