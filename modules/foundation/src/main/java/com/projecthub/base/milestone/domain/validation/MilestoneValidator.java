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
    public void validateCreate(@NotNull final Milestone milestone) {
        log.debug("Validating milestone creation: {}", milestone);
        validateBasicFields(milestone);
        validateInitialStatus(milestone);
        validateInitialProgress(milestone);
        validateBusinessRules(milestone);
    }

    private void validateInitialStatus(@NotNull final Milestone milestone) {
        if (milestone.getStatus() != MilestoneStatus.PENDING) {
            throw new ValidationException("New milestones must start in PENDING status");
        }
    }

    @Override
    public void validateUpdate(@NotNull final Milestone milestone) {
        log.debug("Validating milestone update: {}", milestone);
        validateBasicFields(milestone);
        validateStatusTransition(milestone);
        validateBusinessRules(milestone);
    }

    @Override
    public void validateDelete(@NotNull final Milestone milestone) {
        log.debug("Validating milestone deletion: {}", milestone);
        validateDeletionRules(milestone);
    }

    @Override
    public void validateMilestones(@NotNull final Set<Milestone> milestones) {
        log.debug("Validating {} milestones", milestones.size());
        milestones.forEach(this::validateMilestone);
        validateMilestoneRelationships(milestones);
    }

    // Basic Validations
    private void validateMilestone(@NotNull final Milestone milestone) {
        validateBasicFields(milestone);
        validateDates(milestone);
        validateDependencies(milestone);
    }

    private void validateBasicFields(@NotNull final Milestone milestone) {
        if (milestone.getName() == null || milestone.getName().trim().isEmpty()) {
            throw new ValidationException("Milestone name is required");
        }
        if (milestone.getDueDate() == null) {
            throw new ValidationException("Due date is required");
        }
        if (milestone.getProject() == null) {
            throw new ValidationException("Project association is required");
        }
    }

    // Date Validations
    private void validateDates(@NotNull final Milestone milestone) {
        final LocalDate projectStart = milestone.getProject().getProgress().startDate();
        final LocalDate dueDate = milestone.getDueDate();

        if (dueDate.isBefore(projectStart)) {
            throw new ValidationException("Due date cannot be before project start date");
        }
        if (milestone.getProject().getProgress().endDate() != null &&
            dueDate.isAfter(milestone.getProject().getProgress().endDate())) {
            throw new ValidationException("Due date cannot be after project end date");
        }
    }

    // Status Validations
    private void validateStatusTransition(@NotNull final Milestone milestone) {
        final MilestoneStatus currentStatus = milestone.getStatus();
        final MilestoneStatus targetStatus = milestone.getTargetStatus();

        if (!VALID_TRANSITIONS.get(currentStatus).contains(targetStatus)) {
            throw new ValidationException("Invalid status transition from " + currentStatus + " to " + targetStatus);
        }

        switch (targetStatus) {
            case IN_PROGRESS:
                validateInProgressTransition(milestone);
                break;
            case BLOCKED:
                validateBlockedTransition(milestone);
                break;
            case COMPLETED:
                validateCompletedTransition(milestone);
                break;
            case CANCELLED:
                validateCancelledTransition(milestone);
                break;
            case PENDING:
                validatePendingTransition(milestone);
                break;
        }
    }

    private void validateInProgressTransition(@NotNull final Milestone milestone) {
        log.debug("Validating in-progress transition for milestone: {}", milestone.getId());

        if (!milestone.getDependencies().isEmpty() &&
            !milestone.getDependencies().stream().allMatch(Milestone::isCompleted)) {
            throw new ValidationException("Cannot start milestone with incomplete dependencies");
        }
    }

    private void validateCompletedTransition(final Milestone milestone) {
        if (milestone.getTasks().stream().anyMatch(t -> !t.isCompleted())) {
            throw new ValidationException("Cannot complete milestone with incomplete tasks");
        }
        final double actualProgress = calculateProgress(milestone);
        if (actualProgress < 100.0) {
            throw new ValidationException("Cannot complete milestone with progress less than 100%");
        }
    }

    private void validateBlockedTransition(@NotNull final Milestone milestone) {
        // Check if any dependencies are blocked
        boolean hasBlockedDependency = milestone.getDependencies().stream()
            .anyMatch(Milestone::isBlocked);

        // Check if any dependencies are incomplete
        boolean hasIncompleteDependency = milestone.getDependencies().stream()
            .anyMatch(d -> !d.isCompleted());

        // Check if all tasks are assigned
        boolean hasUnassignedTasks = milestone.getTasks().stream()
            .anyMatch(t -> t.getValue().assignee() == null);

        if (!hasBlockedDependency && !hasIncompleteDependency && !hasUnassignedTasks) {
            throw new ValidationException(
                "Cannot block milestone without blocked/incomplete dependencies or unassigned tasks");
        }

        log.debug("Milestone {} blocked due to: blocked deps={}, incomplete deps={}, unassigned tasks={}",
            milestone.getId(), hasBlockedDependency, hasIncompleteDependency, hasUnassignedTasks);
    }

    private void validateCancelledTransition(final Milestone milestone) {
        if (milestone.isCompleted()) {
            throw new ValidationException("Cannot cancel completed milestone");
        }
    }


    private void validateInitialProgress(final Milestone milestone) {
        if (milestone.getProgress() != 0) {
            throw new ValidationException("New milestone must have 0% progress");
        }
    }

    private void validateProgress(@NotNull final Milestone milestone) {
        log.debug("Validating progress for milestone: {}", milestone.getId());

        // Null check
        final Integer progress = milestone.getProgress();
        if (progress == null) {
            throw new ValidationException("Progress cannot be null");
        }

        // Range validation
        if (progress < 0 || progress > 100) {
            throw new ValidationException("Progress must be between 0 and 100");
        }

        // Task completion alignment
        final double actualProgress = calculateProgress(milestone);
        final double tolerance = 0.5; // Allow 0.5% difference for rounding

        if (Math.abs(progress - actualProgress) > tolerance) {
            throw new ValidationException(
                String.format("Milestone progress (%d%%) does not match task completion rate (%.1f%%)",
                    progress, actualProgress)
            );
        }

        // Status-specific validation
        if (milestone.getStatus() == MilestoneStatus.COMPLETED && progress != 100) {
            throw new ValidationException("Completed milestone must have 100% progress");
        }
    }

    private void validateDeletionRules(@NotNull final Milestone milestone) {
        if (milestone.getStatus() == MilestoneStatus.IN_PROGRESS) {
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

    private void validatePendingTransition(@NotNull final Milestone milestone) {
        log.debug("Validating pending transition for milestone: {}", milestone.getId());

        if (milestone.getStatus() != MilestoneStatus.BLOCKED) {
            throw new ValidationException("Can only transition to PENDING from BLOCKED status");
        }

        if (milestone.getDependencies().stream().anyMatch(d -> !d.isCompleted())) { // Check completion
            throw new ValidationException("All dependencies must be completed before transitioning to PENDING");
        }
    }

    // Dependency Validations
    private void validateDependencies(@NotNull final Milestone milestone) {
        if (milestone.hasCyclicDependencies()) {
            throw new ValidationException("Cyclic dependencies detected");
        }
        if (milestone.getDependencies().stream()
            .anyMatch(dep -> dep.getDueDate().isAfter(milestone.getDueDate()))) {
            throw new ValidationException("Dependency due dates must be before milestone due date");
        }
    }

    // Business Rules
    private void validateBusinessRules(@NotNull final Milestone milestone) {
        validateProgress(milestone);
        validateTaskAlignment(milestone);
        validateDependencyRules(milestone);
    }

    private int calculateProgress(@NotNull final Milestone milestone) {
        if (milestone.getTasks().isEmpty()) {
            return 0;
        }
        final long completed = milestone.getTasks().stream()
            .filter(Task::isCompleted)
            .count();
        return (int) Math.round((completed * 100.0) / milestone.getTasks().size()); // Integer progress
    }

    private void validateTaskAlignment(@NotNull final Milestone milestone) {
        if (milestone.getStatus() == MilestoneStatus.COMPLETED &&
            milestone.getTasks().stream().anyMatch(t -> !t.isCompleted())) {
            throw new ValidationException("Cannot complete milestone with incomplete tasks");
        }
    }

    private void validateDependencyRules(@NotNull final Milestone milestone) {
        if (milestone.getStatus() == MilestoneStatus.PENDING &&
            milestone.getDependencies().stream().anyMatch(d -> d.getStatus() != MilestoneStatus.COMPLETED)) {
            throw new ValidationException("All dependencies must be completed before starting");
        }
    }

    // Relationship Validations
    private void validateMilestoneRelationships(@NotNull final Set<Milestone> milestones) {
        validateDependencyCycles(milestones);
        validateTimelineConsistency(milestones);
    }

    private void validateDependencyCycles(@NotNull final Set<Milestone> milestones) {
        log.debug("Checking dependency cycles in {} milestones", milestones.size());

        // Create adjacency map for milestone dependencies
        final Map<UUID, Set<UUID>> dependencyGraph = new HashMap<>();
        for (final Milestone milestone : milestones) {
            dependencyGraph.put(milestone.getId(),
                milestone.getDependencies().stream()
                    .map(Milestone::getId)
                    .collect(Collectors.toSet()));
        }

        // Check for cycles using DFS
        final Set<UUID> visited = new HashSet<>();
        final Set<UUID> recursionStack = new HashSet<>();

        for (final Milestone milestone : milestones) {
            if (hasCycle(milestone.getId(), dependencyGraph, visited, recursionStack)) {
                throw new ValidationException("Cyclic dependency detected in milestones");
            }
        }
    }

    private boolean hasCycle(final UUID currentId,
                             final Map<UUID, Set<UUID>> dependencyGraph,
                             final Set<UUID> visited,
                             final Set<UUID> recursionStack) {
        if (recursionStack.contains(currentId)) {
            return true;
        }

        if (visited.contains(currentId)) {
            return false;
        }

        visited.add(currentId);
        recursionStack.add(currentId);

        final Set<UUID> dependencies = dependencyGraph.get(currentId);
        if (dependencies != null) {
            for (final UUID dependencyId : dependencies) {
                if (hasCycle(dependencyId, dependencyGraph, visited, recursionStack)) {
                    return true;
                }
            }
        }

        recursionStack.remove(currentId);
        return false;
    }

    private void validateTimelineConsistency(@NotNull final Set<Milestone> milestones) {
        log.debug("Checking timeline consistency for {} milestones", milestones.size());

        for (final Milestone milestone : milestones) {
            if (!milestone.getDependencies().isEmpty()) {  // Explicit check for empty dependencies
                for (final Milestone dependency : milestone.getDependencies()) {
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
