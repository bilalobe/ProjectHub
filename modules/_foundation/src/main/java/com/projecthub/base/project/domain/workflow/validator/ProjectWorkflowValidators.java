package com.projecthub.base.project.domain.workflow.validator;

import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.project.domain.repository.ProjectRepository;
import com.projecthub.base.workflow.domain.model.TransitionValidator;
import com.projecthub.base.workflow.domain.model.WorkflowContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProjectWorkflowValidators {
    private final ProjectRepository projectRepository;

    public TransitionValidator hasTeamAssigned() {
        return context -> {
            Project project = getProject(context);
            return project.getTeamId() != null;
        };
    }

    public TransitionValidator hasMinimumTasks() {
        return context -> {
            Project project = getProject(context);
            return project.getTasks() != null && !project.getTasks().isEmpty();
        };
    }

    public TransitionValidator isProjectStartDateSet() {
        return context -> {
            Project project = getProject(context);
            return project.getStartDate() != null;
        };
    }

    public TransitionValidator canBeCancelled() {
        return context -> {
            Project project = getProject(context);
            return !project.isCompleted();
        };
    }

    private Project getProject(WorkflowContext context) {
        return projectRepository.findById(context.getEntityId())
            .orElseThrow(() -> new IllegalStateException("Project not found"));
    }
}