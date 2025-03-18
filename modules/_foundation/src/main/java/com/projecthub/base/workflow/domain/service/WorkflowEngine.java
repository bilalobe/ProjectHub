package com.projecthub.base.workflow.domain.service;

import com.projecthub.base.workflow.domain.model.*;
import com.projecthub.base.workflow.domain.event.WorkflowTransitionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkflowEngine {
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Attempts to transition an entity to a new state using the provided workflow definition
     * @throws IllegalStateException if the transition is not allowed or validation fails
     */
    @Transactional
    public void transition(WorkflowDefinition workflow, WorkflowContext context) {
        WorkflowState currentState = context.getCurrentState();
        WorkflowState targetState = context.getTargetState();

        // Verify the transition is allowed by the workflow definition
        if (!workflow.isTransitionAllowed(currentState, targetState)) {
            throw new IllegalStateException(
                String.format("Transition from %s to %s is not allowed in workflow %s",
                    currentState.getName(), targetState.getName(), workflow.getName())
            );
        }

        // Find the transition
        WorkflowTransition transition = workflow.getAvailableTransitions(currentState).stream()
            .filter(t -> t.getToState().equals(targetState))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Transition not found"));

        // Validate the transition
        if (!transition.isValid(context)) {
            throw new IllegalStateException("Transition validation failed");
        }

        // Execute transition actions
        transition.executeActions(context);

        // Publish transition event
        publishTransitionEvent(workflow, context, transition);
    }

    private void publishTransitionEvent(WorkflowDefinition workflow, WorkflowContext context, WorkflowTransition transition) {
        WorkflowTransitionEvent event = WorkflowTransitionEvent.builder()
            .workflowId(workflow.getId())
            .entityId(context.getEntityId())
            .entityType(context.getEntityType())
            .fromState(transition.getFromState())
            .toState(transition.getToState())
            .transitionName(transition.getName())
            .build();

        eventPublisher.publishEvent(event);
    }
}