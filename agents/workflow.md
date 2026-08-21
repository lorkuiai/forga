## Workflow

- For Forga SDK changes that add modules, public APIs, policy behavior, resolver contracts, or integrations, create an OpenSpec change first.
- Validate OpenSpec changes with `rtk openspec validate <change-id> --strict --no-interactive`.
- Run targeted Gradle tests and Checkstyle for the touched module before broad validation.
- Run `rtk ./gradlew clean check` before considering SDK implementation complete.
- Gradle build and test commands must use `./gradlew` with the default Gradle environment.
- Keep task status in `openspec/changes/<change-id>/tasks.md` synchronized with completed work.

### Git Commit

- Commit messages must be written in English.
- Use Conventional Commits format: `<type>: <description>` (example: `feat: add scope module`).
- Keep the number of commits per branch minimal.
- Each commit should represent one logical change.
- Do not mix unrelated features in one commit.

### Git Branch Naming

- Branch names must use lowercase letters, digits, slash, and hyphen.
- Branch names should follow: `<type>/<short-description>`.
- `short-description` must be concise kebab-case.
- Supported branch `type` values: `feature`, `bugfix`, `hotfix`, `update`, `refactor`, `chore`, `docs`, and `release`.
