package com.luokuiai.forga.query;

final class QueryValidator {

  private QueryValidator() {
  }

  static String name(String field, String value) {
    if (value == null) {
      throw new IllegalArgumentException(field + " is required");
    }
    String trimmed = value.trim();
    if (trimmed.isEmpty()) {
      throw new IllegalArgumentException(field + " is required");
    }
    if (!isNameStart(trimmed.charAt(0))) {
      throw new IllegalArgumentException(field + " must start with a letter");
    }
    for (int index = 1; index < trimmed.length(); index++) {
      if (!isNamePart(trimmed.charAt(index))) {
        throw new IllegalArgumentException(field + " contains an unsupported character");
      }
    }
    return trimmed;
  }

  private static boolean isNameStart(char value) {
    return value >= 'A' && value <= 'Z' || value >= 'a' && value <= 'z';
  }

  private static boolean isNamePart(char value) {
    return isNameStart(value)
        || value >= '0' && value <= '9'
        || value == '_'
        || value == '-'
        || value == '.'
        || value == ':';
  }
}
