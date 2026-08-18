## 1. MyBatis Auto Integration

- [x] 1.1 Add neutral statement metadata and statement registry APIs in `forga-mybatis`
- [x] 1.2 Add generic subject and request-attribute provider contracts
- [x] 1.3 Implement MyBatis interceptor support that applies one composed constraint for configured
  SELECT statements and fails closed for missing subject or unsupported SQL
- [x] 1.4 Add MyBatis integration tests for configured/unconfigured statements, disabled parity,
  missing subject, unsupported SQL, and one-predicate application
- [x] 1.5 Run `./gradlew :forga-mybatis:test :forga-mybatis:checkstyleMain :forga-mybatis:checkstyleTest`

## 2. Spring Boot Auto Configuration

- [x] 2.1 Add conditional Spring auto-configuration that registers the interceptor only when enabled
- [x] 2.2 Wire policy/resolver validation, statement registry, providers, mappings, and limits through
  generic SDK beans
- [x] 2.3 Add starter tests proving disabled mode registers no interceptor and enabled mode assembles
  the interceptor without host-domain context
- [x] 2.4 Run
  `./gradlew :forga-spring-boot-starter:test :forga-spring-boot-starter:checkstyleMain :forga-spring-boot-starter:checkstyleTest`

## 3. Validation

- [x] 3.1 Run `./gradlew clean check`
- [x] 3.2 Run `openspec validate add-mybatis-spring-auto-integration --strict --no-interactive`
