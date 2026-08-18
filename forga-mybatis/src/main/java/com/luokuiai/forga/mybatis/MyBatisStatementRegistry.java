package com.luokuiai.forga.mybatis;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Immutable registry of statement authorization metadata.
 */
public final class MyBatisStatementRegistry {

  private final Map<String, MyBatisStatementAuthorization> statements;

  /**
   * Creates a statement registry.
   *
   * @param statements statement metadata
   */
  public MyBatisStatementRegistry(List<MyBatisStatementAuthorization> statements) {
    this.statements =
        List.copyOf(statements).stream()
            .collect(
                Collectors.toUnmodifiableMap(
                    MyBatisStatementAuthorization::statementId,
                    Function.identity(),
                    (first, duplicate) -> {
                      throw new IllegalArgumentException(
                          "duplicate statement id: " + duplicate.statementId());
                    }));
  }

  /**
   * Returns metadata for a statement id.
   *
   * @param statementId MyBatis mapped statement id
   * @return authorization metadata when configured
   */
  public Optional<MyBatisStatementAuthorization> find(String statementId) {
    return Optional.ofNullable(statements.get(statementId));
  }

  /**
   * Returns true when no statements are configured.
   *
   * @return true when empty
   */
  public boolean isEmpty() {
    return statements.isEmpty();
  }
}
