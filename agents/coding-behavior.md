# Forga SDK Change Discipline

## 1. Protect The SDK Boundary

- Keep SDK APIs expressed in neutral authorization terms.
- Do not add host-owned business models, persistence tables, or application workflow concepts.
- Do not infer host schema ownership from examples or integrations.
- Optional helper modules can provide reusable patterns, but hosts still map their own data.

## 2. Keep Changes Narrow

- Add only the SDK surface required by the requested capability.
- Prefer existing records, policy expressions, resolver contracts, and query constraint types.
- Do not refactor adjacent modules unless the requested behavior needs it.
- Remove only unused code created by the current change.

## 3. Make Authorization Behavior Verifiable

- For new checks, test allowed, denied, invalid input, and fail-closed outcomes.
- For collection behavior, test bounds, batching, pagination, or query constraints rather than unbounded candidate scans.
- For integrations, test disabled behavior so ordinary host queries remain unchanged.
- Avoid tests that reject business words in abstract SDK APIs; verify module boundaries instead.

## 4. Use OpenSpec For Significant SDK Changes

- New modules, public APIs, policy semantics, resolver contracts, or integration behavior require an OpenSpec change under `openspec/changes/`.
- Keep OpenSpec tasks small enough to verify with targeted Gradle tasks.
- Mark OpenSpec tasks complete only after the matching implementation and checks pass.
