## Build, Test, and Development Commands

### Gradle Usage

- Gradle commands MUST use the project wrapper `./gradlew`.
- Do NOT use a custom Gradle Home or custom `GRADLE_USER_HOME`; use the default environment.
- Prefix shell commands with `rtk`.

### Common Commands

- Full validation: `rtk ./gradlew clean check`
- Module tests: `rtk ./gradlew :forga-core:test`
- Module Checkstyle: `rtk ./gradlew :forga-core:checkstyleMain :forga-core:checkstyleTest`
- Targeted test: `rtk ./gradlew :forga-core:test --tests "com.luokuiai.forga.core.eval.AuthorizationEvaluatorTest"`
- OpenSpec strict validation: `rtk openspec validate <change-id> --strict --no-interactive`

### Completion Bar

- Public API changes require tests and Javadocs.
- Behavioral changes require targeted tests first, then broader validation when the affected surface warrants it.
- Significant changes require OpenSpec artifacts under `openspec/changes/`.
- Implementation is not complete until required Gradle checks and OpenSpec validation pass.
