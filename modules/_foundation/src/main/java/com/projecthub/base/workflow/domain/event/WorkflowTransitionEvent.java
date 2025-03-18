package com.projecthub.base.workflow.domain.event;

import com.projecthub.base.workflow.domain.model.WorkflowState;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

/**
 * Event published when a workflow transition occurs.
 */
@Value
@Builder
public class WorkflowTransitionEvent {
    UUID workflowId;
    UUID entityId;
    String entityType;
    WorkflowState fromState;
    WorkflowState toState;
    String transitionName;
    
    @Builder.Default
    Instant timestamp = Instant.now();
}