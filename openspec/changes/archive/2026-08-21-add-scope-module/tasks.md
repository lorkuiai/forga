## 1. Module Setup

- [x] 1.1 Add `forga-scope` to Gradle settings and create module build configuration.
- [x] 1.2 Create package structure under `com.luokuiai.forga.scope`.

## 2. Scope API

- [x] 2.1 Implement immutable scope value objects with validation and Javadocs.
- [x] 2.2 Implement switch request/result and scoped permission request/result APIs.
- [x] 2.3 Implement active scope and scoped subject provider contracts.

## 3. Policy And Service

- [x] 3.1 Implement scope policy templates for membership, assignment, and denial patterns.
- [x] 3.2 Implement scoped authorization service backed by the existing evaluator.
- [x] 3.3 Implement query constraint helpers for active-scope predicates.

## 4. Tests And Documentation

- [x] 4.1 Add unit tests for validation and provider contracts.
- [x] 4.2 Add unit tests for switch authorization, scoped permission isolation, missing active scope, assignment, and fail-closed behavior.
- [x] 4.3 Add unit tests for generated active-scope query constraints.
- [x] 4.4 Document the `forga-scope` module and host mapping expectations.

## 5. Validation

- [x] 5.1 Run `./gradlew :forga-scope:test :forga-scope:checkstyleMain :forga-scope:checkstyleTest`.
- [x] 5.2 Run `./gradlew clean check`.
- [x] 5.3 Run `openspec validate add-scope-module --strict --no-interactive`.
