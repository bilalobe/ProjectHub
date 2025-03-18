package com.projecthub.base.workflow.api.dto;

import com.projecthub.base.workflow.domain.model.WorkflowState.StateCategory;
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