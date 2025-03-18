package com.projecthub.bootstrap.config.workflow

import com.projecthub.core.domain.model.workflow.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.UUID

@Configuration
class DefaultWorkflowConfig {

    @Bean
    fun defaultProjectWorkflow(): WorkflowDefinition {
        // Create workflow states
        val draft = WorkflowState(
            id = UUID.randomUUID(),
            name = "Draft",
            description = "Project is in draft state",
            category = WorkflowState.StateCategory.INITIAL
        )

        val planning = WorkflowState(
            id = UUID.randomUUID(),
            name = "Planning",
            description = "Project is in planning phase",
            category = WorkflowState.StateCategory.IN_PROGRESS
        )

        val inProgress = WorkflowState(
            id = UUID.randomUUID(),
            name = "In Progress",
            description = "Project is actively being worked on",
            category = WorkflowState.StateCategory.IN_PROGRESS
        )

        val blocked = WorkflowState(
            id = UUID.randomUUID(),
            name = "Blocked",
            description = "Project is blocked by dependencies or issues",
            category = WorkflowState.StateCategory.BLOCKED
        )

        val completed = WorkflowState(
            id = UUID.randomUUID(),
            name = "Completed",
            description = "Project has been completed",
            category = WorkflowState.StateCategory.COMPLETED
        )

        val cancelled = WorkflowState(
            id = UUID.randomUUID(),
            name = "Cancelled",
            description = "Project has been cancelled",
            category = WorkflowState.StateCategory.CANCELLED
        )

        // Define transitions
        val transitions = listOf(
            // From Draft
            createTransition("Start Planning", draft, planning),
            createTransition("Cancel Project", draft, cancelled),

            // From Planning
            createTransition("Start Project", planning, inProgress),
            createTransition("Block Project", planning, blocked),
            createTransition("Cancel Project", planning, cancelled),

            // From In Progress
            createTransition("Block Project", inProgress, blocked),
            createTransition("Complete Project", inProgress, completed),
            createTransition("Cancel Project", inProgress, cancelled),

            // From Blocked
            createTransition("Resume Project", blocked, inProgress),
            createTransition("Cancel Project", blocked, cancelled)
        )

        return WorkflowDefinition(
            id = UUID.randomUUID(),
            name = "Default Project Workflow",
            description = "Standard workflow for projects",
            states = listOf(draft, planning, inProgress, blocked, completed, cancelled),
            transitions = transitions,
            initialState = draft
        )
    }

    private fun createTransition(name: String, from: WorkflowState, to: WorkflowState): WorkflowTransition =
        WorkflowTransition(
            id = UUID.randomUUID(),
            name = name,
            description = "Transition from ${from.name} to ${to.name}",
            fromState = from,
            toState = to,
            actions = emptyList(),
            validators = emptyList()
        )
}