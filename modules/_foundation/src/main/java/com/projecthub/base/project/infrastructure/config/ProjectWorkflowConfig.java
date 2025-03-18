package com.projecthub.base.project.infrastructure.config;

import com.projecthub.base.project.domain.workflow.action.ProjectWorkflowActions;
import com.projecthub.base.project.domain.workflow.validator.ProjectWorkflowValidators;
import com.projecthub.base.workflow.domain.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class ProjectWorkflowConfig {
    private final ProjectWorkflowValidators validators;
    private final ProjectWorkflowActions actions;

    @Bean
    public WorkflowDefinition projectWorkflow() {
        // Create workflow states
        WorkflowState draft = WorkflowState.builder()
            .id(UUID.randomUUID())
            .name("Draft")
            .description("Project is in draft state")
            .category(WorkflowState.StateCategory.INITIAL)
            .build();

        WorkflowState planning = WorkflowState.builder()
            .id(UUID.randomUUID())
            .name("Planning")
            .description("Project is in planning phase")
            .category(WorkflowState.StateCategory.IN_PROGRESS)
            .build();

        WorkflowState inProgress = WorkflowState.builder()
            .id(UUID.randomUUID())
            .name("In Progress")
            .description("Project is actively being worked on")
            .category(WorkflowState.StateCategory.IN_PROGRESS)
            .build();

        WorkflowState blocked = WorkflowState.builder()
            .id(UUID.randomUUID())
            .name("Blocked")
            .description("Project is blocked by dependencies or issues")
            .category(WorkflowState.StateCategory.BLOCKED)
            .build();

        WorkflowState completed = WorkflowState.builder()
            .id(UUID.randomUUID())
            .name("Completed")
            .description("Project has been completed")
            .category(WorkflowState.StateCategory.COMPLETED)
            .build();

        WorkflowState cancelled = WorkflowState.builder()
            .id(UUID.randomUUID())
            .name("Cancelled")
            .description("Project has been cancelled")
            .category(WorkflowState.StateCategory.CANCELLED)
            .build();

        // Define transitions with validators and actions
        List<WorkflowTransition> transitions = List.of(
            // From Draft
            createTransition("Start Planning", draft, planning,
                List.of(validators.hasMinimumTasks()),
                List.of(actions.notifyProjectStatusChanged())),

            createTransition("Cancel Project", draft, cancelled,
                List.of(validators.canBeCancelled()),
                List.of(actions.notifyProjectStatusChanged())),

            // From Planning
            createTransition("Start Project", planning, inProgress,
                List.of(
                    validators.hasTeamAssigned(),
                    validators.isProjectStartDateSet()
                ),
                List.of(
                    actions.updateStartDate(),
                    actions.notifyProjectStatusChanged()
                )),

            createTransition("Block Project", planning, blocked,
                List.of(),
                List.of(actions.notifyProjectStatusChanged())),

            createTransition("Cancel Project", planning, cancelled,
                List.of(validators.canBeCancelled()),
                List.of(actions.notifyProjectStatusChanged())),

            // From In Progress
            createTransition("Block Project", inProgress, blocked,
                List.of(),
                List.of(actions.notifyProjectStatusChanged())),

            createTransition("Complete Project", inProgress, completed,
                List.of(validators.hasMinimumTasks()),
                List.of(
                    actions.updateCompletionDate(),
                    actions.notifyProjectStatusChanged()
                )),

            createTransition("Cancel Project", inProgress, cancelled,
                List.of(validators.canBeCancelled()),
                List.of(actions.notifyProjectStatusChanged())),

            // From Blocked
            createTransition("Resume Project", blocked, inProgress,
                List.of(),
                List.of(actions.notifyProjectStatusChanged())),

            createTransition("Cancel Project", blocked, cancelled,
                List.of(validators.canBeCancelled()),
                List.of(actions.notifyProjectStatusChanged()))
        );

        // Create workflow definition
        return WorkflowDefinition.builder()
            .id(UUID.randomUUID())
            .name("Project Workflow")
            .description("Standard workflow for projects")
            .states(List.of(draft, planning, inProgress, blocked, completed, cancelled))
            .transitions(transitions)
            .initialState(draft)
            .build();
    }

    private WorkflowTransition createTransition(
            String name,
            WorkflowState from,
            WorkflowState to,
            List<TransitionValidator> validators,
            List<TransitionAction> actions) {
        return WorkflowTransition.builder()
            .id(UUID.randomUUID())
            .name(name)
            .description(String.format("Transition from %s to %s", from.getName(), to.getName()))
            .fromState(from)
            .toState(to)
            .validators(validators)
            .actions(actions)
            .build();
    }
}