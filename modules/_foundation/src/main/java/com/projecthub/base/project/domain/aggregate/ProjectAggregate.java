package com.projecthub.base.project.domain.aggregate;

import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.project.domain.enums.ProjectStatus;
import com.projecthub.base.project.domain.event.ProjectStatusChangedEvent;
import com.projecthub.base.shared.domain.AggregateRoot;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Aggregate root for Project domain.
 * Encapsulates project-related business rules and state changes.
 */
@Getter
public class ProjectAggregate implements AggregateRoot {
    private final Project project;
    
    public ProjectAggregate(Project project) {
        this.project = project;
    }

    public void updateStatus(ProjectStatus newStatus, UUID initiatorId) {
        ProjectStatus oldStatus = project.getStatus();
        if (oldStatus != newStatus) {
            validateStatusTransition(oldStatus, newStatus);
            project.setStatus(newStatus);
            
            // If completing project, set end date
            if (newStatus == ProjectStatus.COMPLETED) {
                project.setEndDate(LocalDate.now());
            }
            
            // Raise domain event
            registerEvent(new ProjectStatusChangedEvent(project, oldStatus, initiatorId));
        }
    }

    private void validateStatusTransition(ProjectStatus oldStatus, ProjectStatus newStatus) {
        // Add validation logic for status transitions
        if (oldStatus == ProjectStatus.COMPLETED && newStatus != ProjectStatus.REOPENED) {
            throw new IllegalStateException("Completed projects can only be reopened");
        }
        
        if (oldStatus == ProjectStatus.CANCELLED && newStatus != ProjectStatus.REOPENED) {
            throw new IllegalStateException("Cancelled projects can only be reopened");
        }
    }
}