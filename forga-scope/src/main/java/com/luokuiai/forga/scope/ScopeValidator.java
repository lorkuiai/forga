package com.luokuiai.forga.scope;

final class ScopeValidator {

  private static final int MAX_KIND_LENGTH = 128;

  private static final int MAX_VALUE_LENGTH = 1024;

  private ScopeValidator() {
  }

  static String kind(String field, String value) {
    String normalized = required(field, value, MAX_KIND_LENGTH);
    if (!isKindStart(normalized.charAt(0))) {
      throw new IllegalArgumentException(field + " must start with a letter");
    }
    for (int index = 1; index < normalized.length(); index++) {
      char current = normalized.charAt(index);
      if (!isKindPart(current)) {
        throw new IllegalArgumentException(field + " contains an unsupported character");
      }
    }
    return normalized;
  }

  static String value(String field, String value) {
    return required(field, value, MAX_VALUE_LENGTH);
  }

  private static String required(String field, String value, int maxLength) {
    if (value == null) {
      throw new IllegalArgumentException(field + " is required");
    }
    String trimmed = value.trim();
    if (trimmed.isEmpty()) {
      throw new IllegalArgumentException(field + " is required");
    }
    if (trimmed.length() > maxLength) {
      throw new IllegalArgumentException(field + " is too long");
    }
    for (int index = 0; index < trimmed.length(); index++) {
      if (Character.isISOControl(trimmed.charAt(index))) {
        throw new IllegalArgumentException(field + " contains a control character");
      }
    }
    return trimmed;
  }

  private static boolean isKindStart(char value) {
    return value >= 'A' && value <= 'Z' || value >= 'a' && value <= 'z';
  }

  private static boolean isKindPart(char value) {
    return isKindStart(value)
        || value >= '0' && value <= '9'
        || value == '_'
        || value == '-'
        || value == '.'
        || value == ':';
  }
}
