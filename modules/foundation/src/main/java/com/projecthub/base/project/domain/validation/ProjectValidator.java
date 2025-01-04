package com.projecthub.base.project.domain.validation;

import com.projecthub.base.milestone.domain.entity.Milestone;
import com.projecthub.base.milestone.domain.enums.MilestoneStatus;
import com.projecthub.base.milestone.domain.validation.MilestoneValidation;
import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.project.domain.enums.ProjectStatus;
import com.projecthub.base.shared.exception.ValidationException;
import com.projecthub.base.task.domain.entity.Task;
import com.projecthub.base.task.domain.validation.TaskValidator;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
public class ProjectValidator {
    private final TaskValidator taskValidator;
    private final MilestoneValidation milestoneValidator;

    public ProjectValidator(TaskValidator taskValidator, MilestoneValidation milestoneValidator) {
        this.taskValidator = taskValidator;
        this.milestoneValidator = milestoneValidator;
    }

    public void validateCreate(final @NotNull Project project) {
        log.debug("Validating project creation: {}", project);
        validateBasicFields(project);
        validateInitialStatus(project);
        validateBusinessRules(project);
    }

    public void validateUpdate(final @NotNull Project project) {
        log.debug("Validating project update: {}", project);
        validateBasicFields(project);
        validateStatusTransition(project);
        validateBusinessRules(project);
    }

    public void validateDelete(final @NotNull Project project) {
        log.debug("Validating project deletion: {}", project);
        validateDeletionRules(project);
    }

    private void validateBasicFields(final Project project) {
        if (project.getName() == null || project.getName().trim().isEmpty()) {
            throw new ValidationException("Project name is required");
        }
        if (project.getTeam() == null) {
            throw new ValidationException("Project team is required");
        }
    }

    private void validateInitialStatus(final Project project) {
        if (project.getStatus() != ProjectStatus.DRAFT) {
            throw new ValidationException("New projects must start in DRAFT status");
        }
    }

    private void validateStatusTransition(final Project project) {
        switch (project.getStatus()) {
            case DRAFT -> validateDraftTransition(project);
            case ACTIVE -> validateActiveTransition(project);
            case COMPLETED, CANCELLED -> throw new ValidationException("Cannot transition from terminal state");
            default -> throw new ValidationException("Invalid status");
        }
    }

    private void validateDraftTransition(final Project project) {
        if (!project.getTeam().hasCapacity()) {
            throw new ValidationException("Team at capacity");
        }
    }

    private void validateActiveTransition(final Project project) {
        if (project.getTasks().isEmpty()) {
            throw new ValidationException("Project needs at least one task");
        }

        if (!project.getMilestones().isEmpty() && project.getMilestones().stream().noneMatch(m -> {
            MilestoneStatus status = m.getStatus(); // Store to avoid repeated calls and handle null
            return status != null && status == MilestoneStatus.PENDING; // Handle null and correct comparison
        })) {
            throw new ValidationException("Project needs at least one started milestone");
        } else if (project.getMilestones().stream().allMatch(m -> m.getStatus() != MilestoneStatus.PENDING)) {
            throw new ValidationException("At least one milestone must be started if milestones exist.");
        }
    }

    private void validateDeletionRules(final Project project) {
        if (project.isActive()) {
            throw new ValidationException("Cannot delete active project");
        }
        if (project.hasIncompleteTasks()) {
            throw new ValidationException("Cannot delete project with incomplete tasks");
        }
        if (project.getMilestones().stream().anyMatch(Milestone::isActive)) {
            throw new ValidationException("Cannot delete project with active milestones");
        }
    }

    private void validateBusinessRules(final Project project) {
        validateTeamCapacity(project);
        validateTaskCompletionRules(project);
        validateMilestoneAlignment(project);
    }

    private void validateTaskCompletionRules(final Project project) {
        taskValidator.validateTasks((Set<Task>) project.getTasks());
    }

    private void validateMilestoneAlignment(final Project project) {
        milestoneValidator.validateMilestones((Set<Milestone>) project.getMilestones());
    }

    private void validateTeamCapacity(final Project project) {
        if (project.getTeam() == null || !project.getTeam().hasCapacity()) {
            throw new ValidationException("Project team is at capacity");
        }
    }
}
