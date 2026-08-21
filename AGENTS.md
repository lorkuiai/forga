## OpenSpec Instructions

Spec-driven workflow for proposals, specs, architecture changes, and other change-managed work.

- Propose: `/opsx:propose <change-name>` · Apply: `/opsx:apply <change-name>` · Archive: `/opsx:archive <change-name>`
- Single source of truth: `openspec/config.yaml`
- Specs: `openspec/specs/<capability>/spec.md` — read on-demand for the capability under change.
- Active changes: `openspec/changes/<change-id>/`

## AGENTS Index

Open only the file relevant to the current question. If a change spans multiple surfaces, open only the matching files for those surfaces.

- `@/agents/project-structure.md` — directories, module boundaries, package ownership
- `@/agents/build-test-development.md` — commands, build steps, tests
- `@/agents/coding-behavior.md` — Forga SDK change discipline
- `@/agents/java-sdk-coding-style.md` — Java SDK conventions
- `@/agents/workflow.md` — change workflow, verification, Git conventions
- `@/agents/rtk.md` — RTK usage rules and token-optimized command examples

## Project Instructions

- Java 17, Gradle Wrapper, and Google Java formatting conventions are required.
- Forga core must remain domain-neutral and must not contain tenant-specific concepts.
- Business data and authorization relationships remain owned by host applications.
- Storage integrations must be optional implementations of resolver contracts.
- Public APIs require Javadoc and tests.
- Run `./gradlew check` before considering implementation complete.
- Significant changes use OpenSpec under `openspec/changes/`.
