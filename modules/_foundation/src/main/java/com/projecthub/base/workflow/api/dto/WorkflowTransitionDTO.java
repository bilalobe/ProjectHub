package com.projecthub.base.workflow.api.dto;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class WorkflowTransitionDTO {
    UUID id;
    String name;
    String description;
    UUID fromStateId;
    UUID toStateId;
}