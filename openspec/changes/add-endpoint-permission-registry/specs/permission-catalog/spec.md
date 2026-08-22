## ADDED Requirements

### Requirement: Endpoint-contributed permission definitions
Spring Web endpoint registrations that require permissions SHALL contribute their definitions to the
ordinary permission catalog without adding Spring or endpoint metadata to the core catalog model.

#### Scenario: Registered permission enters catalog
- **WHEN** an endpoint contributor registers a required permission definition
- **THEN** catalog assembly includes that definition for host synchronization

#### Scenario: Permission protects several endpoints
- **WHEN** several endpoint registrations use the same identical permission definition
- **THEN** catalog assembly contains the definition once

#### Scenario: Endpoint definitions conflict
- **WHEN** endpoint registrations use one permission reference with different display or module data
- **THEN** registration assembly fails before catalog synchronization

#### Scenario: Permit-all endpoint is registered
- **WHEN** an endpoint contributor declares a handler as permit-all
- **THEN** no permission definition is added to the catalog for that declaration
