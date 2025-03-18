package com.projecthub.base.workflow.api.dto;

import lombok.Builder;
import lombok.Value;

import java.util.Map;
import java.util.UUID;

@Value
@Builder
public class WorkflowTransitionRequestDTO {
    UUID entityId;
    String entityType;
    UUID currentStateId;
    UUID targetStateId;
    
    @Builder.Default
    Map<String, Object> metadata = Map.of();
}