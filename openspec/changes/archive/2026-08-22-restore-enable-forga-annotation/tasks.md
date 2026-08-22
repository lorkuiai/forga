## 1. Annotation-Based Enablement

- [x] 1.1 Add the public `@EnableForga` marker and internal composed enablement condition.
- [x] 1.2 Replace property-based conditions on every Forga auto-configuration.

## 2. Verification And Documentation

- [x] 2.1 Migrate Starter tests to annotated user configurations and prove legacy properties have no
  effect.
- [x] 2.2 Update README setup and disabled behavior documentation for `@EnableForga`.
- [x] 2.3 Run Starter tests and Checkstyle, full `clean check`, and strict OpenSpec validation.

## 3. Remove Duplicate Enablement Model

- [x] 3.1 Remove `ForgaIntegrationProperties` and make annotation-enabled MyBatis assembly
  unconditional.
- [x] 3.2 Change direct runtime assembly to accept `EvaluationLimits` and update affected tests.
- [x] 3.3 Run Starter tests and Checkstyle, full `clean check`, and strict OpenSpec validation.
