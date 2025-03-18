package com.projecthub.infrastructure.adapter.in.dto;

import com.projecthub.core.domain.model.workflow.WorkflowState.StateCategory;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class WorkflowStateDTO {
    UUID id;
    String name;
    String description;
    StateCategory category;
}