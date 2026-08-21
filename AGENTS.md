# Project Instructions

- Java 17, Gradle Wrapper, and Google Java formatting conventions are required.
- Forga core must remain domain-neutral and must not contain tenant-specific concepts.
- Business data and authorization relationships remain owned by host applications.
- Storage integrations must be optional implementations of resolver contracts.
- Public APIs require Javadoc and tests.
- Run `./gradlew check` before considering implementation complete.
- Significant changes use OpenSpec under `openspec/changes/`.
