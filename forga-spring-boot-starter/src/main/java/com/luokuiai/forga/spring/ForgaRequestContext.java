package com.luokuiai.forga.spring;

import com.luokuiai.forga.core.model.AttributeRef;
import com.luokuiai.forga.core.model.ConsistencyToken;
import com.luokuiai.forga.core.model.SubjectRef;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Request-scoped authorization context.
 *
 * @param subject request subject
 * @param attributes request attributes
 * @param consistency optional consistency token
 * @param deadline optional deadline
 */
public record ForgaRequestContext(
    SubjectRef subject,
    Map<AttributeRef, String> attributes,
    Optional<ConsistencyToken> consistency,
    Optional<Instant> deadline) {

  /**
   * Creates a request context.
   *
   * @param subject request subject
   * @param attributes request attributes
   * @param consistency optional consistency token
   * @param deadline optional deadline
   */
  public ForgaRequestContext {
    subject = Objects.requireNonNull(subject, "subject is required");
    attributes = Map.copyOf(attributes);
    consistency = consistency == null ? Optional.empty() : consistency;
    deadline = deadline == null ? Optional.empty() : deadline;
  }
}
