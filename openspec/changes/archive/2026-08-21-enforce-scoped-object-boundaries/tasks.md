## 1. Scope Boundary Contracts

- [x] 1.1 Add immutable cross-scope access request and host resolver contracts with validation and Javadocs.
- [x] 1.2 Add tests for resolver request validation and deny-by-default behavior.

## 2. Strict Scoped Authorization

- [x] 2.1 Add strict `ScopedAuthorizationService` constructors while preserving existing construction.
- [x] 2.2 Enforce object scope resolution, same-scope matching, explicit cross-scope grants, and fail-closed resolver behavior.
- [x] 2.3 Add tests for same-scope allow, mismatch denial, explicit grant allow, missing ownership, resolver failure, and legacy compatibility.

## 3. Query Composition And Documentation

- [x] 3.1 Add a typed active-or-granted scope query constraint helper and tests.
- [x] 3.2 Update README examples, security boundary guidance, and migration notes.

## 4. Validation

- [x] 4.1 Run `./gradlew :forga-scope:test :forga-scope:checkstyleMain :forga-scope:checkstyleTest`.
- [x] 4.2 Run `./gradlew clean check`.
- [x] 4.3 Run `openspec validate enforce-scoped-object-boundaries --strict --no-interactive`.
