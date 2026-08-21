## ADDED Requirements

### Requirement: Framework-neutral authenticated subject
Forga SHALL expose a domain-neutral provider that returns the current authenticated `SubjectRef`
without depending on a Web, authentication, or persistence framework.

#### Scenario: Authenticated subject available
- **WHEN** an integration requests the current subject in an authenticated context
- **THEN** the provider returns the mapped `SubjectRef`

#### Scenario: Authenticated subject unavailable
- **WHEN** an integration requests the current subject without an authenticated context
- **THEN** the provider returns empty and configured authorization fails closed

### Requirement: Sa-Token authentication adapter
Forga SHALL provide an optional Sa-Token adapter that maps an injectable `StpLogic` login id to a
canonical `user` subject without evaluating Sa-Token permissions or roles.

#### Scenario: Sa-Token login maps to subject
- **WHEN** the selected `StpLogic` reports an authenticated login id
- **THEN** the adapter returns a `SubjectRef` containing type `user` and the login id

#### Scenario: Sa-Token login is absent
- **WHEN** the selected `StpLogic` reports no login
- **THEN** the adapter returns no authenticated subject

### Requirement: Spring Security authentication adapter
Forga SHALL provide an optional Spring Security adapter that maps an authenticated, non-anonymous
`Authentication` to a canonical `user` subject without making decisions from granted authorities.

#### Scenario: Spring authentication maps to subject
- **WHEN** the security context contains an authenticated non-anonymous principal
- **THEN** the adapter returns a `SubjectRef` using type `user` and the authentication name

#### Scenario: Anonymous authentication is not a subject
- **WHEN** the security context is empty, unauthenticated, or anonymous
- **THEN** the adapter returns no authenticated subject

### Requirement: Optional adapter dependencies
Authentication framework dependencies MUST remain outside `forga-core` and MUST be optional for
hosts that do not use the corresponding framework.

#### Scenario: Core is used standalone
- **WHEN** a host depends only on `forga-core`
- **THEN** neither Sa-Token nor Spring Security is required on its classpath

### Requirement: Unambiguous authentication provider
Enabled Spring Boot integration MUST discover authentication providers without a provider-selection
property and MUST require exactly one `AuthenticatedSubjectProvider`.

#### Scenario: One authentication provider is available
- **WHEN** enabled integration discovers exactly one authenticated-subject provider
- **THEN** startup succeeds and integrations use that provider

#### Scenario: No authentication provider is available
- **WHEN** enabled integration discovers no authenticated-subject provider
- **THEN** startup fails with an authentication-provider configuration error

#### Scenario: Multiple authentication providers are available
- **WHEN** enabled integration discovers multiple authenticated-subject providers
- **THEN** startup fails instead of selecting one provider implicitly
