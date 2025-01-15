package com.projecthub.base.milestone.domain.validation;

import com.projecthub.base.milestone.domain.entity.Milestone;
import com.projecthub.base.milestone.domain.enums.MilestoneStatus;
import com.projecthub.base.shared.exception.ValidationException;
import com.projecthub.base.task.domain.entity.Task;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Validator for Milestone entities. Organized in validation groups:
 * 1. Basic validations (name, dates, fields)
 * 2. Status transitions
 * 3. Progress tracking
 * 4. Dependencies
 * 5. Business rules
 * 6. Relationship validations
 */
@Slf4j
@Component
public class MilestoneValidator implements MilestoneValidation {

    private static final Map<MilestoneStatus, Set<MilestoneStatus>> VALID_TRANSITIONS = Map.of(
        MilestoneStatus.PENDING, Set.of(MilestoneStatus.IN_PROGRESS, MilestoneStatus.BLOCKED, MilestoneStatus.CANCELLED),
        MilestoneStatus.IN_PROGRESS, Set.of(MilestoneStatus.COMPLETED, MilestoneStatus.BLOCKED, MilestoneStatus.CANCELLED),
        MilestoneStatus.BLOCKED, Set.of(MilestoneStatus.PENDING, MilestoneStatus.CANCELLED),
        MilestoneStatus.COMPLETED, Set.of(),
        MilestoneStatus.CANCELLED, Set.of()
    );

    @Override
    public void validateCreate(@NotNull Milestone milestone) {
        MilestoneValidator.log.debug("Validating milestone creation: {}", milestone);
        this.validateBasicFields(milestone);
        this.validateInitialStatus(milestone);
        this.validateInitialProgress(milestone);
        this.validateBusinessRules(milestone);
    }

    private void validateInitialStatus(@NotNull Milestone milestone) {
        if (MilestoneStatus.PENDING != milestone.getStatus()) {
            throw new ValidationException("New milestones must start in PENDING status");
        }
    }

    @Override
    public void validateUpdate(@NotNull Milestone milestone) {
        MilestoneValidator.log.debug("Validating milestone update: {}", milestone);
        this.validateBasicFields(milestone);
        this.validateStatusTransition(milestone);
        this.validateBusinessRules(milestone);
    }

    @Override
    public void validateDelete(@NotNull Milestone milestone) {
        MilestoneValidator.log.debug("Validating milestone deletion: {}", milestone);
        this.validateDeletionRules(milestone);
    }

    @Override
    public void validateMilestones(@NotNull Set<Milestone> milestones) {
        MilestoneValidator.log.debug("Validating {} milestones", milestones.size());
        milestones.forEach(this::validateMilestone);
        this.validateMilestoneRelationships(milestones);
    }

    // Basic Validations
    private void validateMilestone(@NotNull Milestone milestone) {
        this.validateBasicFields(milestone);
        this.validateDates(milestone);
        this.validateDependencies(milestone);
    }

    private void validateBasicFields(@NotNull Milestone milestone) {
        if (null == milestone.getName() || milestone.getName().trim().isEmpty()) {
            throw new ValidationException("Milestone name is required");
        }
        if (null == milestone.getDueDate()) {
            throw new ValidationException("Due date is required");
        }
        if (null == milestone.getProject()) {
            throw new ValidationException("Project association is required");
        }
    }

    // Date Validations
    private void validateDates(@NotNull Milestone milestone) {
        LocalDate projectStart = milestone.getProject().getProgress().startDate();
        LocalDate dueDate = milestone.getDueDate();

        if (dueDate.isBefore(projectStart)) {
            throw new ValidationException("Due date cannot be before project start date");
        }
        if (null != milestone.getProject().getProgress().endDate() &&
            dueDate.isAfter(milestone.getProject().getProgress().endDate())) {
            throw new ValidationException("Due date cannot be after project end date");
        }
    }

    // Status Validations
    private void validateStatusTransition(@NotNull Milestone milestone) {
        MilestoneStatus currentStatus = milestone.getStatus();
        MilestoneStatus targetStatus = milestone.getTargetStatus();

        if (!MilestoneValidator.VALID_TRANSITIONS.get(currentStatus).contains(targetStatus)) {
            throw new ValidationException("Invalid status transition from " + currentStatus + " to " + targetStatus);
        }

        switch (targetStatus) {
            case IN_PROGRESS:
                this.validateInProgressTransition(milestone);
                break;
            case BLOCKED:
                this.validateBlockedTransition(milestone);
                break;
            case COMPLETED:
                this.validateCompletedTransition(milestone);
                break;
            case CANCELLED:
                this.validateCancelledTransition(milestone);
                break;
            case PENDING:
                this.validatePendingTransition(milestone);
                break;
        }
    }

    private void validateInProgressTransition(@NotNull Milestone milestone) {
        MilestoneValidator.log.debug("Validating in-progress transition for milestone: {}", milestone.getId());

        if (!milestone.getDependencies().isEmpty() &&
            !milestone.getDependencies().stream().allMatch(Milestone::isCompleted)) {
            throw new ValidationException("Cannot start milestone with incomplete dependencies");
        }
    }

    private void validateCompletedTransition(Milestone milestone) {
        if (milestone.getTasks().stream().anyMatch(t -> !t.isCompleted())) {
            throw new ValidationException("Cannot complete milestone with incomplete tasks");
        }
        double actualProgress = this.calculateProgress(milestone);
        if (100.0 > actualProgress) {
            throw new ValidationException("Cannot complete milestone with progress less than 100%");
        }
    }

    private void validateBlockedTransition(@NotNull Milestone milestone) {
        // Check if any dependencies are blocked
        final boolean hasBlockedDependency = milestone.getDependencies().stream()
            .anyMatch(Milestone::isBlocked);

        // Check if any dependencies are incomplete
        final boolean hasIncompleteDependency = milestone.getDependencies().stream()
            .anyMatch(d -> !d.isCompleted());

        // Check if all tasks are assigned
        final boolean hasUnassignedTasks = milestone.getTasks().stream()
            .anyMatch(t -> null == t.getValue().assignee());

        if (!hasBlockedDependency && !hasIncompleteDependency && !hasUnassignedTasks) {
            throw new ValidationException(
                "Cannot block milestone without blocked/incomplete dependencies or unassigned tasks");
        }

        MilestoneValidator.log.debug("Milestone {} blocked due to: blocked deps={}, incomplete deps={}, unassigned tasks={}",
            milestone.getId(), hasBlockedDependency, hasIncompleteDependency, hasUnassignedTasks);
    }

    private void validateCancelledTransition(Milestone milestone) {
        if (milestone.isCompleted()) {
            throw new ValidationException("Cannot cancel completed milestone");
        }
    }


    private void validateInitialProgress(Milestone milestone) {
        if (0 != milestone.getProgress()) {
            throw new ValidationException("New milestone must have 0% progress");
        }
    }

    private void validateProgress(@NotNull Milestone milestone) {
        MilestoneValidator.log.debug("Validating progress for milestone: {}", milestone.getId());

        // Null check
        Integer progress = milestone.getProgress();
        if (null == progress) {
            throw new ValidationException("Progress cannot be null");
        }

        // Range validation
        if (0 > progress || 100 < progress) {
            throw new ValidationException("Progress must be between 0 and 100");
        }

        // Task completion alignment
        double actualProgress = this.calculateProgress(milestone);
        final double tolerance = 0.5; // Allow 0.5% difference for rounding

        if (tolerance < Math.abs(progress - actualProgress)) {
            throw new ValidationException(
                String.format("Milestone progress (%d%%) does not match task completion rate (%.1f%%)",
                    progress, actualProgress)
            );
        }

        // Status-specific validation
        if (MilestoneStatus.COMPLETED == milestone.getStatus() && 100 != progress) {
            throw new ValidationException("Completed milestone must have 100% progress");
        }
    }

    private void validateDeletionRules(@NotNull Milestone milestone) {
        if (MilestoneStatus.IN_PROGRESS == milestone.getStatus()) {
            throw new ValidationException("Cannot delete in-progress milestone");
        }
        if (!milestone.getTasks().isEmpty()) {
            throw new ValidationException("Cannot delete milestone with existing tasks");
        }
        // Check if other milestones depend on this one
        if (milestone.getProject().getMilestones().stream()
            .anyMatch(m -> m.getDependencies().contains(milestone))) {
            throw new ValidationException("Cannot delete milestone that others depend on");
        }
    }

    private void validatePendingTransition(@NotNull Milestone milestone) {
        MilestoneValidator.log.debug("Validating pending transition for milestone: {}", milestone.getId());

        if (MilestoneStatus.BLOCKED != milestone.getStatus()) {
            throw new ValidationException("Can only transition to PENDING from BLOCKED status");
        }

        if (milestone.getDependencies().stream().anyMatch(d -> !d.isCompleted())) { // Check completion
            throw new ValidationException("All dependencies must be completed before transitioning to PENDING");
        }
    }

    // Dependency Validations
    private void validateDependencies(@NotNull Milestone milestone) {
        if (milestone.hasCyclicDependencies()) {
            throw new ValidationException("Cyclic dependencies detected");
        }
        if (milestone.getDependencies().stream()
            .anyMatch(dep -> dep.getDueDate().isAfter(milestone.getDueDate()))) {
            throw new ValidationException("Dependency due dates must be before milestone due date");
        }
    }

    // Business Rules
    private void validateBusinessRules(@NotNull Milestone milestone) {
        this.validateProgress(milestone);
        this.validateTaskAlignment(milestone);
        this.validateDependencyRules(milestone);
    }

    private int calculateProgress(@NotNull Milestone milestone) {
        if (milestone.getTasks().isEmpty()) {
            return 0;
        }
        long completed = milestone.getTasks().stream()
            .filter(Task::isCompleted)
            .count();
        return (int) Math.round((completed * 100.0) / milestone.getTasks().size()); // Integer progress
    }

    private void validateTaskAlignment(@NotNull Milestone milestone) {
        if (MilestoneStatus.COMPLETED == milestone.getStatus() &&
            milestone.getTasks().stream().anyMatch(t -> !t.isCompleted())) {
            throw new ValidationException("Cannot complete milestone with incomplete tasks");
        }
    }

    private void validateDependencyRules(@NotNull Milestone milestone) {
        if (MilestoneStatus.PENDING == milestone.getStatus() &&
            milestone.getDependencies().stream().anyMatch(d -> MilestoneStatus.COMPLETED != d.getStatus())) {
            throw new ValidationException("All dependencies must be completed before starting");
        }
    }

    // Relationship Validations
    private void validateMilestoneRelationships(@NotNull Set<Milestone> milestones) {
        this.validateDependencyCycles(milestones);
        this.validateTimelineConsistency(milestones);
    }

    private void validateDependencyCycles(@NotNull Set<Milestone> milestones) {
        MilestoneValidator.log.debug("Checking dependency cycles in {} milestones", milestones.size());

        // Create adjacency map for milestone dependencies
        Map<UUID, Set<UUID>> dependencyGraph = new HashMap<>();
        for (Milestone milestone : milestones) {
            dependencyGraph.put(milestone.getId(),
                milestone.getDependencies().stream()
                    .map(Milestone::getId)
                    .collect(Collectors.toSet()));
        }

        // Check for cycles using DFS
        Set<UUID> visited = new HashSet<>();
        Set<UUID> recursionStack = new HashSet<>();

        for (Milestone milestone : milestones) {
            if (this.hasCycle(milestone.getId(), dependencyGraph, visited, recursionStack)) {
                throw new ValidationException("Cyclic dependency detected in milestones");
            }
        }
    }

    private boolean hasCycle(UUID currentId,
                             Map<UUID, Set<UUID>> dependencyGraph,
                             Set<UUID> visited,
                             Set<UUID> recursionStack) {
        if (recursionStack.contains(currentId)) {
            return true;
        }

        if (visited.contains(currentId)) {
            return false;
        }

        visited.add(currentId);
        recursionStack.add(currentId);

        Set<UUID> dependencies = dependencyGraph.get(currentId);
        if (null != dependencies) {
            for (UUID dependencyId : dependencies) {
                if (this.hasCycle(dependencyId, dependencyGraph, visited, recursionStack)) {
                    return true;
                }
            }
        }

        recursionStack.remove(currentId);
        return false;
    }

    private void validateTimelineConsistency(@NotNull Set<Milestone> milestones) {
        MilestoneValidator.log.debug("Checking timeline consistency for {} milestones", milestones.size());

        for (Milestone milestone : milestones) {
            if (!milestone.getDependencies().isEmpty()) {  // Explicit check for empty dependencies
                for (Milestone dependency : milestone.getDependencies()) {
                    // Allow equal due dates - adjust as needed for strict ordering
                    if (!dependency.getDueDate().isBefore(milestone.getDueDate())) {
                        throw new ValidationException(
                            String.format("Milestone '%s' due date must not be before its dependency '%s' due date",
                                milestone.getName(), dependency.getName())
                        );
                    }
                }
            }
        }
    }
}
