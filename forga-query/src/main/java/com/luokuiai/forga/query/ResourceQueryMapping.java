package com.luokuiai.forga.query;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Allowlist of query fields available for one caller-defined resource.
 *
 * @param resource mapped resource
 * @param fieldNames allowlisted field names
 */
public record ResourceQueryMapping(QueryResource resource, Set<String> fieldNames) {

  /**
   * Creates a resource query mapping.
   *
   * @param resource mapped resource
   * @param fieldNames allowlisted field names
   */
  public ResourceQueryMapping {
    resource = Objects.requireNonNull(resource, "resource is required");
    fieldNames =
        fieldNames.stream()
            .map(field -> QueryValidator.name("field name", field))
            .collect(Collectors.toUnmodifiableSet());
    if (fieldNames.isEmpty()) {
      throw new IllegalArgumentException("fieldNames must not be empty");
    }
  }

  /**
   * Creates a mapping from a field collection.
   *
   * @param resource mapped resource
   * @param fieldNames allowlisted field names
   * @return resource query mapping
   */
  public static ResourceQueryMapping of(QueryResource resource, Collection<String> fieldNames) {
    return new ResourceQueryMapping(resource, Set.copyOf(fieldNames));
  }

  /**
   * Returns an allowlisted field.
   *
   * @param name field name
   * @return query field
   */
  public QueryField field(String name) {
    String fieldName = QueryValidator.name("field name", name);
    if (!fieldNames.contains(fieldName)) {
      throw new UnknownQueryFieldException("unknown field: " + fieldName);
    }
    return new QueryField(resource, fieldName);
  }
}
