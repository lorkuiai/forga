package com.luokuiai.forga.mybatis;

import com.luokuiai.forga.query.AuthorizedListQuery;
import com.luokuiai.forga.query.BooleanConstraint;
import com.luokuiai.forga.query.BooleanOperator;
import com.luokuiai.forga.query.ExistsConstraint;
import com.luokuiai.forga.query.PredicateConstraint;
import com.luokuiai.forga.query.PredicateOperator;
import com.luokuiai.forga.query.QueryConstraint;
import com.luokuiai.forga.query.QueryCorrelation;
import com.luokuiai.forga.query.QueryFieldOperand;
import com.luokuiai.forga.query.QueryJoin;
import com.luokuiai.forga.query.QueryOrdering;
import com.luokuiai.forga.query.QueryOperand;
import com.luokuiai.forga.query.QueryParameter;
import com.luokuiai.forga.query.QueryProjection;
import com.luokuiai.forga.query.QueryResource;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Translates typed constraints into one parameterized MyBatis SQL predicate fragment.
 */
public final class MyBatisConstraintTranslator {

  private final Map<QueryResource, MyBatisResourceMapping> mappings;

  /**
   * Creates a translator.
   *
   * @param mappings allowlisted resource mappings
   */
  public MyBatisConstraintTranslator(Map<QueryResource, MyBatisResourceMapping> mappings) {
    this.mappings = Map.copyOf(Objects.requireNonNull(mappings, "mappings are required"));
  }

  /**
   * Translates a typed constraint.
   *
   * @param constraint typed constraint
   * @return bound SQL predicate
   */
  public MyBatisBoundConstraint translate(QueryConstraint constraint) {
    List<QueryParameter> parameters = new ArrayList<>();
    String sql = translateConstraint(constraint, parameters);
    return new MyBatisBoundConstraint(sql, parameters);
  }

  /**
   * Translates and applies a set-based authorized list query to a SELECT statement.
   *
   * @param sql original SELECT SQL
   * @param query authorized list query
   * @return SQL with authorization rowset join, projections, and ordering
   */
  public MyBatisBoundSql translateAuthorizedList(String sql, AuthorizedListQuery query) {
    Objects.requireNonNull(query, "query is required");
    String original = Objects.requireNonNull(sql, "sql is required").trim();
    if (original.isBlank()) {
      throw new IllegalArgumentException("sql is required");
    }
    List<QueryParameter> parameters = new ArrayList<>();
    String selectSql = addProjections(original, query.projections());
    String joinedSql = addJoin(selectSql, query);
    String filteredSql = addWhere(joinedSql, translateConstraint(query.where(), parameters));
    String orderedSql = addOrderings(filteredSql, query.orderings());
    return new MyBatisBoundSql(orderedSql, parameters);
  }

  private String translateConstraint(
      QueryConstraint constraint, List<QueryParameter> parameters) {
    if (constraint instanceof PredicateConstraint predicate) {
      return translatePredicate(predicate, parameters);
    }
    if (constraint instanceof BooleanConstraint booleanConstraint) {
      return translateBoolean(booleanConstraint, parameters);
    }
    if (constraint instanceof ExistsConstraint exists) {
      return translateExists(exists, parameters);
    }
    throw new MyBatisTranslationException("unsupported constraint node");
  }

  private String translatePredicate(
      PredicateConstraint predicate, List<QueryParameter> parameters) {
    return column(predicate.left())
        + " "
        + operator(predicate.operator())
        + " "
        + operand(predicate.right(), parameters);
  }

  private String translateBoolean(
      BooleanConstraint constraint, List<QueryParameter> parameters) {
    if (constraint.operator() == BooleanOperator.NOT) {
      return "NOT (" + translateConstraint(constraint.constraints().get(0), parameters) + ")";
    }
    String separator = constraint.operator() == BooleanOperator.AND ? " AND " : " OR ";
    return constraint.constraints().stream()
        .map(child -> "(" + translateConstraint(child, parameters) + ")")
        .collect(Collectors.joining(separator));
  }

  private String translateExists(ExistsConstraint exists, List<QueryParameter> parameters) {
    MyBatisResourceMapping mapping = mapping(exists.resource());
    String joins =
        exists.joins().stream()
            .map(this::translateJoin)
            .collect(Collectors.joining(" AND "));
    String where = translateConstraint(exists.where(), parameters);
    return "EXISTS (SELECT 1 FROM "
        + mapping.tableReference()
        + " WHERE "
        + joins
        + " AND "
        + where
        + ")";
  }

  private String translateJoin(QueryJoin join) {
    return join.correlations().stream()
        .map(this::translateCorrelation)
        .collect(Collectors.joining(" AND "));
  }

  private String translateCorrelation(QueryCorrelation correlation) {
    return column(correlation.outer()) + " = " + column(correlation.inner());
  }

  private String addProjections(String sql, List<QueryProjection> projections) {
    if (projections.isEmpty()) {
      return sql;
    }
    int fromIndex = keywordIndex(sql, " from ");
    if (fromIndex < 0 || !sql.regionMatches(true, 0, "select ", 0, 7)) {
      throw new MyBatisTranslationException("authorized list query requires SELECT ... FROM");
    }
    String projectionSql =
        projections.stream()
            .map(projection -> column(projection.field()) + " AS " + projection.alias())
            .collect(Collectors.joining(", "));
    return sql.substring(0, fromIndex) + ", " + projectionSql + sql.substring(fromIndex);
  }

  private String addJoin(String sql, AuthorizedListQuery query) {
    String joinSql =
        query.join().correlations().stream()
            .map(this::translateCorrelation)
            .collect(Collectors.joining(" AND "));
    MyBatisResourceMapping rowset = mapping(query.join().rowset().resource());
    int insertionIndex =
        firstClauseIndex(sql, List.of(" where ", " order by ", " limit ", " offset "));
    String suffix = insertionIndex < 0 ? "" : sql.substring(insertionIndex);
    String prefix = insertionIndex < 0 ? sql : sql.substring(0, insertionIndex);
    return prefix + " JOIN " + rowset.tableReference() + " ON " + joinSql + suffix;
  }

  private String addWhere(String sql, String where) {
    int orderIndex = firstClauseIndex(sql, List.of(" order by ", " limit ", " offset "));
    String suffix = orderIndex < 0 ? "" : sql.substring(orderIndex);
    String prefix = orderIndex < 0 ? sql : sql.substring(0, orderIndex);
    String separator = prefix.toLowerCase(Locale.ROOT).contains(" where ") ? " AND " : " WHERE ";
    return prefix + separator + "(" + where + ")" + suffix;
  }

  private String addOrderings(String sql, List<QueryOrdering> orderings) {
    if (orderings.isEmpty()) {
      return sql;
    }
    String orderingSql =
        orderings.stream()
            .map(ordering -> column(ordering.field()) + " " + ordering.direction().name())
            .collect(Collectors.joining(", "));
    int orderIndex = keywordIndex(sql, " order by ");
    int limitIndex = firstClauseIndex(sql, List.of(" limit ", " offset "));
    if (orderIndex >= 0) {
      String prefix = sql.substring(0, orderIndex + " order by ".length());
      String existing =
          limitIndex < 0
              ? sql.substring(orderIndex + " order by ".length())
              : sql.substring(orderIndex + " order by ".length(), limitIndex);
      String suffix = limitIndex < 0 ? "" : sql.substring(limitIndex);
      return prefix + orderingSql + ", " + existing.strip() + suffix;
    }
    String suffix = limitIndex < 0 ? "" : sql.substring(limitIndex);
    String prefix = limitIndex < 0 ? sql : sql.substring(0, limitIndex);
    return prefix + " ORDER BY " + orderingSql + suffix;
  }

  private String operand(QueryOperand operand, List<QueryParameter> parameters) {
    if (operand instanceof QueryParameter parameter) {
      parameters.add(parameter);
      return "#{forga.parameters." + parameter.name() + "}";
    }
    if (operand instanceof QueryFieldOperand fieldOperand) {
      return column(fieldOperand.field());
    }
    throw new MyBatisTranslationException("unsupported operand node");
  }

  private String column(com.luokuiai.forga.query.QueryField field) {
    return mapping(field.resource()).columnReference(field);
  }

  private MyBatisResourceMapping mapping(QueryResource resource) {
    MyBatisResourceMapping mapping = mappings.get(resource);
    if (mapping == null) {
      throw new MyBatisTranslationException("resource is not mapped: " + resource.type());
    }
    return mapping;
  }

  private static int firstClauseIndex(String sql, List<String> clauses) {
    return clauses.stream()
        .mapToInt(clause -> keywordIndex(sql, clause))
        .filter(index -> index >= 0)
        .min()
        .orElse(-1);
  }

  private static int keywordIndex(String sql, String keyword) {
    return sql.toLowerCase(Locale.ROOT).indexOf(keyword);
  }

  private static String operator(PredicateOperator operator) {
    return switch (operator) {
      case EQUALS -> "=";
      case NOT_EQUALS -> "<>";
      case IN -> "IN";
      case GREATER_THAN -> ">";
      case GREATER_THAN_OR_EQUALS -> ">=";
      case LESS_THAN -> "<";
      case LESS_THAN_OR_EQUALS -> "<=";
    };
  }
}
