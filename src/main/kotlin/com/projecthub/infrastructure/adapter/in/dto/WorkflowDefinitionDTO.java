package com.projecthub.infrastructure.adapter.in.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@Builder
public class WorkflowDefinitionDTO {
    UUID id;
    String name;
    String description;
    List<WorkflowStateDTO> states;
    List<WorkflowTransitionDTO> transitions;
    UUID initialStateId;
}