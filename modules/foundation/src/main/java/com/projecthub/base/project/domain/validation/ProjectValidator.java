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

    public ProjectValidator(final TaskValidator taskValidator, final MilestoneValidation milestoneValidator) {
        this.taskValidator = taskValidator;
        this.milestoneValidator = milestoneValidator;
    }

    public void validateCreate(@NotNull Project project) {
        ProjectValidator.log.debug("Validating project creation: {}", project);
        this.validateBasicFields(project);
        this.validateInitialStatus(project);
        this.validateBusinessRules(project);
    }

    public void validateUpdate(@NotNull Project project) {
        ProjectValidator.log.debug("Validating project update: {}", project);
        this.validateBasicFields(project);
        this.validateStatusTransition(project);
        this.validateBusinessRules(project);
    }

    public void validateDelete(@NotNull Project project) {
        ProjectValidator.log.debug("Validating project deletion: {}", project);
        this.validateDeletionRules(project);
    }

    private void validateBasicFields(Project project) {
        if (null == project.getName() || project.getName().trim().isEmpty()) {
            throw new ValidationException("Project name is required");
        }
        if (null == project.getTeam()) {
            throw new ValidationException("Project team is required");
        }
    }

    private void validateInitialStatus(Project project) {
        if (ProjectStatus.DRAFT != project.getStatus()) {
            throw new ValidationException("New projects must start in DRAFT status");
        }
    }

    private void validateStatusTransition(Project project) {
        switch (project.getStatus()) {
            case DRAFT -> this.validateDraftTransition(project);
            case ACTIVE -> this.validateActiveTransition(project);
            case COMPLETED, CANCELLED -> throw new ValidationException("Cannot transition from terminal state");
            default -> throw new ValidationException("Invalid status");
        }
    }

    private void validateDraftTransition(Project project) {
        if (!project.getTeam().hasCapacity()) {
            throw new ValidationException("Team at capacity");
        }
    }

    private void validateActiveTransition(Project project) {
        if (project.getTasks().isEmpty()) {
            throw new ValidationException("Project needs at least one task");
        }

        if (!project.getMilestones().isEmpty() && project.getMilestones().stream().noneMatch(m -> {
            final MilestoneStatus status = m.getStatus(); // Store to avoid repeated calls and handle null
            return null != status && MilestoneStatus.PENDING == status; // Handle null and correct comparison
        })) {
            throw new ValidationException("Project needs at least one started milestone");
        } else if (project.getMilestones().stream().allMatch(m -> MilestoneStatus.PENDING != m.getStatus())) {
            throw new ValidationException("At least one milestone must be started if milestones exist.");
        }
    }

    private void validateDeletionRules(Project project) {
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

    private void validateBusinessRules(Project project) {
        this.validateTeamCapacity(project);
        this.validateTaskCompletionRules(project);
        this.validateMilestoneAlignment(project);
    }

    private void validateTaskCompletionRules(Project project) {
        this.taskValidator.validateTasks((Set<Task>) project.getTasks());
    }

    private void validateMilestoneAlignment(Project project) {
        this.milestoneValidator.validateMilestones((Set<Milestone>) project.getMilestones());
    }

    private void validateTeamCapacity(Project project) {
        if (null == project.getTeam() || !project.getTeam().hasCapacity()) {
            throw new ValidationException("Project team is at capacity");
        }
    }
}
