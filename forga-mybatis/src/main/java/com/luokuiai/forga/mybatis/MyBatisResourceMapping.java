package com.luokuiai.forga.mybatis;

import com.luokuiai.forga.query.QueryField;
import com.luokuiai.forga.query.QueryResource;
import java.util.Map;
import java.util.Objects;

/**
 * Allowlisted MyBatis table and column mapping for one query resource.
 *
 * @param resource mapped resource
 * @param table mapped table name
 * @param columns field-name to column-name mapping
 */
public record MyBatisResourceMapping(
    QueryResource resource, String table, Map<String, String> columns) {

  /**
   * Creates a MyBatis resource mapping.
   *
   * @param resource mapped resource
   * @param table mapped table name
   * @param columns field-name to column-name mapping
   */
  public MyBatisResourceMapping {
    resource = Objects.requireNonNull(resource, "resource is required");
    table = identifier("table", table);
    columns = Map.copyOf(Objects.requireNonNull(columns, "columns are required"));
    if (columns.isEmpty()) {
      throw new IllegalArgumentException("columns must not be empty");
    }
    columns.values().forEach(column -> identifier("column", column));
  }

  String tableReference() {
    return table;
  }

  String columnReference(QueryField field) {
    if (!resource.equals(field.resource())) {
      throw new MyBatisTranslationException("field resource is not mapped: " + field.name());
    }
    String column = columns.get(field.name());
    if (column == null) {
      throw new MyBatisTranslationException("field is not mapped: " + field.name());
    }
    return table + "." + column;
  }

  private static String identifier(String field, String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(field + " is required");
    }
    String trimmed = value.trim();
    for (int index = 0; index < trimmed.length(); index++) {
      char current = trimmed.charAt(index);
      if (!isIdentifierPart(current)) {
        throw new IllegalArgumentException(field + " contains an unsupported character");
      }
    }
    return trimmed;
  }

  private static boolean isIdentifierPart(char value) {
    return value >= 'A' && value <= 'Z'
        || value >= 'a' && value <= 'z'
        || value >= '0' && value <= '9'
        || value == '_'
        || value == '.';
  }
}
