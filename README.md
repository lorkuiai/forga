# Forga

Forga is an embedded, domain-neutral authorization SDK for Java applications. It evaluates RBAC,
ReBAC, and ABAC policies over relationships and attributes supplied by the host application.

Forga is not an identity system, permission-management product, tenant framework, or relationship
database. Host applications keep their own accounts, business resources, relationship storage, and
permission-management UI. Forga provides the authorization model, bounded evaluator, resolver
contracts, query constraints, and optional framework integrations.

## When To Use It

Use Forga when an application needs authorization such as:

- A subject can view or edit a resource because of a direct role or relationship.
- A resource inherits permissions through a group, parent object, folder, workspace, project, or
  other host-defined boundary.
- A subject can operate in multiple boundaries and must select an active boundary before acting.
- A list query must be constrained by authorization instead of loading rows and checking them one by
  one.
- Authorization data already exists in host tables or services and should not be copied into a
  Forga-owned store.

## Modules

- `forga-core`: references, policy expressions, compiled policies, bounded `check`, `bulkCheck`,
  and `listObjects` evaluation.
- `forga-resolver-api`: host-owned relationship and attribute resolver contracts.
- `forga-query`: typed query constraints for pushing authorization into host queries.
- `forga-mybatis`: MyBatis SQL translation and statement interception helpers.
- `forga-spring-boot-starter`: opt-in runtime assembly and MyBatis auto-configuration.
- `forga-scope`: scope switching, active-scope checks, acting context, and scope query helpers.
- `forga-spring-web`: resource-code annotations and Spring MVC interceptor integration.

## Design Model

Forga evaluates opaque references:

```text
subject + permission + object + attributes -> decision
```

The host decides what the names mean:

```text
SubjectRef("account", "alice")
ObjectRef("document", "doc-1")
RelationRef("viewer")
PermissionRef("view")
AttributeRef("region")
```

Forga compares these values and evaluates policy expressions. It does not assign business meaning
to object types, subject types, relations, permissions, caveats, attributes, or scopes.

The key design boundary is:

```text
Host application owns data.
Forga owns authorization evaluation.
```

## Authorization Styles

Forga supports RBAC, ReBAC, and ABAC using one policy model.

RBAC is represented as relationships from a role-like object or scope to a subject:

```text
scope:alpha#admin@subject:alice
permission manage = relation(admin)
```

ReBAC is represented as relationships between subjects, objects, and object sets:

```text
document:roadmap#viewer@subject:alice
document:roadmap#parent@folder:strategy#member
folder:strategy#member@subject:alice
```

ABAC is represented with caveats and request attributes:

```text
permission view = caveat(relation(viewer), business_hours)
attributes: business_hours=true
```

These styles can be composed in one expression with union, intersection, exclusion, traversal, and
caveat nodes.

## Basic Check

Define a policy:

```java
RelationRef viewer = new RelationRef("viewer");
PermissionRef view = new PermissionRef("view");

CompiledPolicy policy =
    PolicyCompiler.compile(
        new PolicyDefinition(Map.of(view, PermissionExpression.relation(viewer))),
        ResolverCapabilities.of(List.of(viewer), List.of()));
```

Create an evaluator with a host resolver:

```java
AuthorizationEvaluator evaluator =
    new AuthorizationEvaluator(policy, relationshipLookup, EvaluationLimits.defaults());

CheckDecision decision =
    evaluator.check(
        new CheckRequest(
            new ObjectRef("document", "doc-1"),
            new PermissionRef("view"),
            new SubjectRef("account", "alice")));
```

`decision.allowed()` is true only when the resolver can prove the relationship required by the
policy. Unknown permissions, resolver failures, cycle detection, limit exhaustion, and consistency
conflicts fail closed.

## Host Resolvers

Applications expose existing authorization data through resolver contracts. A resolver can read
from any host-owned table, cache, service, or graph, but it returns neutral Forga references.

`forga-resolver-api` provides higher-level resolver contracts:

```java
RelationshipResolver resolver = ...;
ResolverRegistry registry = new ResolverRegistry(List.of(resolver));
```

`forga-core` evaluates against lower-level lookup contracts:

```java
RelationshipLookup relationshipLookup = requests -> ...;
ObjectListingLookup objectListingLookup = requests -> ...;
```

Forward resolution powers `check` and `bulkCheck`. Reverse resolution powers `listObjects`.
Attribute resolution is used for caveats and allowlisted query mappings.

Forga does not require a Forga-owned relationship table. Hosts may store relationships in their own
schema, derive them from business tables, or resolve them from external services.

## Object Listing

`listObjects` discovers objects from reverse relationship resolver pages:

```java
ListObjectsResponse response =
    evaluator.listObjects(
        new ListObjectsRequest(
            "document",
            new PermissionRef("view"),
            new SubjectRef("account", "alice"),
            50));
```

This is not a table scan plus per-row `check`. The host resolver must provide reverse lookup pages.
Objects that are not discoverable from reverse relationships are outside graph listing. For normal
business list pages, use query constraints instead.

Listing cursors are opaque and bound to the request identity, policy fingerprint, consistency
context, and resolver continuation state. Reusing a cursor with different request inputs fails
closed.

## Query Constraints

`forga-query` represents authorization filters as typed fields, parameters, predicates, joins,
correlated existence checks, and boolean composition. `forga-mybatis` translates only allowlisted
fields into parameterized SQL fragments.

This is the path for business list pages:

```text
host request -> subject + active scope -> query constraint -> parameterized SQL
```

Unknown fields, unsafe identifiers, or unsupported constraint nodes are rejected before SQL
execution.

For ReBAC list pages, prefer an authorized rowset plan instead of per-row `check` calls. The host
maps a business resource and an authorization rowset, then Forga generates one SQL statement that
joins them before filtering, ordering, and pagination:

```text
business_resource
JOIN authorized_rowset ON resource.id = authorized_rowset.object_id
WHERE authorized_rowset.subject_id = #{forga.parameters.subject}
ORDER BY authorized_rowset.rank DESC, resource.created_at DESC
LIMIT ...
```

The authorization rowset can be a host table, view, materialized view, or queryable relation
projection. It can expose fields such as relation, scope, rank, source, or assignment status.
Those fields can be selected with stable aliases and used for ordering before pagination:

```java
AuthorizedListQuery listQuery =
    QueryConstraintGenerator.authorizedRowset(
        taskMapping,
        accessMapping,
        "id",
        "object_id",
        QueryConstraint.predicate(
            accessMapping.field("subject_id"),
            PredicateOperator.EQUALS,
            new QueryParameter("subject", QueryValueType.STRING)),
        List.of(new QueryProjection(accessMapping.field("relation"), "forga_relation")),
        List.of(new QueryOrdering(accessMapping.field("rank"), QuerySortDirection.DESC)));

MyBatisAuthorizationBoundary boundary =
    MyBatisAuthorizationBoundary.list("task-list", listQuery);
```

This keeps pagination correct because the database filters and sorts the authorized rowset before
returning a page. It also keeps relationship context available for the UI without loading a page of
business rows and authorizing each row separately.

## Scope And Concurrent Roles

`forga-scope` handles applications where one subject can operate under multiple authorization
boundaries. A scope can represent a host-owned boundary such as a workspace, project, organization,
department, tenant, or another partition. Scope names are examples; hosts map their own ids to
`ScopeRef`.

The scope package provides:

- `ScopeRef`: opaque boundary reference.
- `ActiveScope`: selected boundary for the current request.
- `ScopedSubject`: subject plus optional active scope.
- `ActingScopeContext`: original subject, acting subject, and active scope.
- `ScopeSwitchRequest` / `ScopeSwitchDecision`: check whether a subject can enter a scope.
- `ScopedPermissionRequest` / `ScopedPermissionDecision`: check a permission under active scope.
- `ScopePolicyTemplates`: `member`, `assigned`, `denied`, and `enter` policy helpers.
- `ScopeQueryConstraints`: parameterized predicates for active-scope list filtering.

Concurrent cross-boundary roles should be stored as subject-scope relationships, not account-owned
state. For example:

```text
scope:alpha#member@subject:alice
scope:beta#assigned@subject:alice
scope:beta#denied@subject:bob
```

An application may return an aggregated list for switching UI:

```json
[
  {
    "scopeType": "workspace",
    "scopeId": "alpha",
    "relation": "member",
    "role": "admin",
    "primary": true
  },
  {
    "scopeType": "workspace",
    "scopeId": "beta",
    "relation": "assigned",
    "role": "reviewer",
    "primary": false
  }
]
```

That list is display state. Authorization still evaluates the underlying relationships and the
selected `ActiveScope`.

Scope switch example:

```java
ScopedAuthorizationService service = new ScopedAuthorizationService(evaluator);

ScopeSwitchDecision decision =
    service.canSwitch(
        new ScopeSwitchRequest(
            new SubjectRef("account", "alice"),
            new ScopeRef("workspace", "beta"),
            ScopePolicyTemplates.ENTER));
```

Scoped permission example:

```java
ScopedPermissionDecision decision =
    service.check(
        new ScopedPermissionRequest(
            new ObjectRef("task", "task-1"),
            new PermissionRef("edit"),
            ScopedSubject.of(
                new SubjectRef("account", "alice"),
                new ActiveScope(new ScopeRef("workspace", "beta")))));
```

The service first verifies that the subject can enter the active scope, then evaluates the requested
resource permission.

## Spring Web Resource Annotations

Business systems usually already have a resource catalog and controller permissions. Forga's Spring
Web integration keeps that shape instead of forcing endpoints to call SDK internals directly.

```java
public final class AdminResources {
  public static final String MEETING_VIEW = "rsc:meeting:view";
  public static final String MEETING_MAINTAIN = "rsc:meeting:maintain";

  private AdminResources() {}
}
```

```java
@RequiresResource(AdminResources.MEETING_VIEW)
public MeetingDetail getMeeting(String meetingId) {
  ...
}
```

The resource code remains host-defined. The host provides one adapter that maps the code and current
request context to Forga checks:

```java
ResourceCheckAdapter adapter =
    invocation -> {
      ResourceRule rule = resourceCatalog.require(invocation.resourceCode());
      CheckDecision decision =
          evaluator.check(
              new CheckRequest(
                  rule.objectRef(invocation),
                  rule.permission(),
                  subjectProvider.currentSubject()));
      return ResourceAuthorizationDecision.from(invocation.resourceCode(), decision);
    };
```

Register the service and MVC interceptor in application configuration:

```java
@Bean
ResourceAuthorizationService resourceAuthorizationService(ResourceCheckAdapter adapter) {
  return new ResourceAuthorizationService(adapter);
}

@Override
public void addInterceptors(InterceptorRegistry registry) {
  registry.addInterceptor(new RequiresResourceInterceptor(resourceAuthorizationService));
}
```

For service-layer code or non-MVC entry points, use the same facade:

```java
resourceAuthorizationService.requireResource(AdminResources.MEETING_MAINTAIN);
```

`@RequiresResource(RequiresResource.NONE)` explicitly marks an endpoint as requiring no resource
permission. If the module is not registered as an MVC interceptor, annotations have no runtime
effect. Collection authorization should still use query constraints or MyBatis rowsets so list
pages are filtered, sorted, and paginated in SQL rather than checked row by row.

## MyBatis And Spring Integration

`forga-mybatis` can apply translated query constraints to configured MyBatis statements. The host
registers statement authorization metadata and supplies the current subject/request attributes.

Important integration behavior:

- Configured statements receive authorization constraints.
- Unconfigured statements are left unchanged.
- Disabled integration leaves ordinary business SQL unchanged.
- Missing subject or unsupported configured SQL fails closed.
- Only allowlisted fields are translated into SQL.

`forga-spring-boot-starter` assembles optional runtime components and MyBatis integration when
enabled. Applications still provide host-specific resolvers, subject providers, active-scope
providers, and statement mappings.

## Consistency And Limits

Each evaluation can carry one opaque consistency token. Resolvers may establish the token on the
first read; conflicting tokens fail closed.

Evaluation and listing enforce configured bounds:

- max depth
- max visited nodes
- max resolver calls
- max intermediate results
- page size
- batch size
- deadline
- cycle detection

## Disabled Behavior

The starter is opt-in. When disabled, it assembles no runtime components and the MyBatis applicator
returns the original SQL unchanged, with no required authorization request context.

## Build

```bash
./gradlew clean check
```

GitHub Actions runs the same Gradle check on pull requests targeting `main` or `develop` and on
pushes to those branches.
