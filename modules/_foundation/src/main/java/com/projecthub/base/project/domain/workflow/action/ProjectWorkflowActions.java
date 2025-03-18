package com.projecthub.base.project.domain.workflow.action;

import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.project.domain.event.ProjectStatusChangedEvent;
import com.projecthub.base.project.domain.repository.ProjectRepository;
import com.projecthub.base.workflow.domain.model.TransitionAction;
import com.projecthub.base.workflow.domain.model.WorkflowContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ProjectWorkflowActions {
    private final ProjectRepository projectRepository;
    private final ApplicationEventPublisher eventPublisher;

    public TransitionAction updateStartDate() {
        return context -> {
            Project project = getProject(context);
            if (project.getStartDate() == null) {
                project.setStartDate(LocalDateTime.now());
                projectRepository.save(project);
            }
        };
    }

    public TransitionAction updateCompletionDate() {
        return context -> {
            Project project = getProject(context);
            project.setCompletionDate(LocalDateTime.now());
            projectRepository.save(project);
        };
    }

    public TransitionAction notifyProjectStatusChanged() {
        return context -> {
            Project project = getProject(context);
            ProjectStatusChangedEvent event = new ProjectStatusChangedEvent(
                project.getId(),
                context.getCurrentState().getName(),
                context.getTargetState().getName(),
                LocalDateTime.now()
            );
            eventPublisher.publishEvent(event);
        };
    }

    private Project getProject(WorkflowContext context) {
        return projectRepository.findById(context.getEntityId())
            .orElseThrow(() -> new IllegalStateException("Project not found"));
    }
}