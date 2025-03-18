package com.projecthub.base.project.application.service;

import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.project.domain.repository.ProjectRepository;
import com.projecthub.base.workflow.domain.model.WorkflowContext;
import com.projecthub.base.workflow.domain.model.WorkflowDefinition;
import com.projecthub.base.workflow.domain.model.WorkflowState;
import com.projecthub.base.workflow.domain.service.WorkflowEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectWorkflowService {
    private final WorkflowEngine workflowEngine;
    private final WorkflowDefinition projectWorkflow;
    private final ProjectRepository projectRepository;

    @Transactional
    public void transitionProject(UUID projectId, UUID targetStateId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        WorkflowState currentState = findStateById(project.getWorkflowStateId());
        WorkflowState targetState = findStateById(targetStateId);

        WorkflowContext context = WorkflowContext.builder()
            .entityId(projectId)
            .entityType("project")
            .currentState(currentState)
            .targetState(targetState)
            .build();

        // Execute the transition
        workflowEngine.transition(projectWorkflow, context);

        // Update project state
        project.setWorkflowStateId(targetStateId);
        projectRepository.save(project);
    }

    private WorkflowState findStateById(UUID stateId) {
        return projectWorkflow.getStates().stream()
            .filter(state -> state.getId().equals(stateId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Workflow state not found"));
    }

    public WorkflowDefinition getProjectWorkflow() {
        return projectWorkflow;
    }
}