package com.luokuiai.forga.mybatis;

import com.luokuiai.forga.query.BooleanConstraint;
import com.luokuiai.forga.query.BooleanOperator;
import com.luokuiai.forga.query.ExistsConstraint;
import com.luokuiai.forga.query.PredicateConstraint;
import com.luokuiai.forga.query.PredicateOperator;
import com.luokuiai.forga.query.QueryConstraint;
import com.luokuiai.forga.query.QueryCorrelation;
import com.luokuiai.forga.query.QueryFieldOperand;
import com.luokuiai.forga.query.QueryJoin;
import com.luokuiai.forga.query.QueryOperand;
import com.luokuiai.forga.query.QueryParameter;
import com.luokuiai.forga.query.QueryResource;
import java.util.ArrayList;
import java.util.List;
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
