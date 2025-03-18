package com.projecthub.core.application.service

import com.projecthub.core.domain.model.workflow.*
import com.projecthub.core.domain.event.WorkflowTransitionEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WorkflowEngine(private val eventPublisher: ApplicationEventPublisher) {

    @Transactional
    fun transition(workflow: WorkflowDefinition, context: WorkflowContext) {
        val currentState = context.getCurrentState()
        val targetState = context.getTargetState()

        if (!workflow.isTransitionAllowed(currentState, targetState)) {
            throw IllegalStateException(
                "Transition from ${currentState.getName()} to ${targetState.getName()} is not allowed in workflow ${workflow.getName()}"
            )
        }

        val transition = workflow.getAvailableTransitions(currentState).stream()
            .filter { t -> t.getToState() == targetState }
            .findFirst()
            .orElseThrow { IllegalStateException("Transition not found") }

        if (!transition.isValid(context)) {
            throw IllegalStateException("Transition validation failed")
        }

        transition.executeActions(context)
        publishTransitionEvent(workflow, context, transition)
    }

    private fun publishTransitionEvent(
        workflow: WorkflowDefinition,
        context: WorkflowContext,
        transition: WorkflowTransition
    ) {
        val event = WorkflowTransitionEvent.builder()
            .workflowId(workflow.getId())
            .entityId(context.getEntityId())
            .entityType(context.getEntityType())
            .fromState(transition.getFromState())
            .toState(transition.getToState())
            .transitionName(transition.getName())
            .build()

        eventPublisher.publishEvent(event)
    }
}
