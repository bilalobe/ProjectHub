package com.projecthub.domain.milestone.validation

import com.projecthub.domain.milestone.Milestone
import com.projecthub.domain.milestone.MilestoneStatus
import com.projecthub.domain.milestone.exception.*
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class MilestoneValidationService {

    fun validateCreation(
        name: String,
        description: String,
        dueDate: LocalDate
    ) {
        require(name.isNotBlank()) { "Milestone name cannot be empty" }
        require(description.isNotBlank()) { "Milestone description cannot be empty" }
        require(!dueDate.isBefore(LocalDate.now())) { "Due date cannot be in the past" }
    }

    fun validateStatusTransition(
        milestone: Milestone,
        newStatus: MilestoneStatus
    ) {
        if (milestone.isCompleted) {
            throw CompletedMilestoneModificationException(milestone.id)
        }

        when (newStatus) {
            MilestoneStatus.COMPLETED -> validateCompletionPreconditions(milestone)
            MilestoneStatus.IN_PROGRESS -> validateStartPreconditions(milestone)
            MilestoneStatus.BLOCKED -> validateBlockedPreconditions(milestone)
            else -> {} // Other transitions don't need special validation
        }
    }

    fun validateDueDateChange(
        milestone: Milestone,
        newDueDate: LocalDate
    ) {
        if (milestone.isCompleted) {
            throw CompletedMilestoneModificationException(milestone.id)
        }

        if (newDueDate.isBefore(LocalDate.now())) {
            throw MilestoneConstraintViolationException("Due date cannot be in the past")
        }

        // Check dependency constraints
        milestone.dependencies.forEach { dependency ->
            if (newDueDate.isBefore(dependency.dueDate)) {
                throw DependencyDueDateConflictException(
                    "Due date cannot be before dependency ${dependency.id} due date"
                )
            }
        }
    }

    fun validateDependencyAddition(
        milestone: Milestone,
        dependency: Milestone
    ) {
        if (milestone.isCompleted) {
            throw CompletedMilestoneModificationException(milestone.id)
        }

        if (dependency.isCompleted) {
            throw MilestoneConstraintViolationException(
                "Cannot add completed milestone as dependency"
            )
        }

        if (hasCyclicDependency(milestone, dependency, mutableSetOf())) {
            throw CyclicDependencyException(milestone.id, dependency.id)
        }

        if (milestone.dueDate.isBefore(dependency.dueDate)) {
            throw DependencyDueDateConflictException(
                "Milestone due date cannot be before its dependency's due date"
            )
        }
    }

    private fun validateCompletionPreconditions(milestone: Milestone) {
        if (!milestone.dependencies.all { it.isCompleted }) {
            throw MilestoneCompletionPreConditionException(
                "All dependencies must be completed before completing milestone"
            )
        }

        if (milestone.tasks.any { !it.isCompleted }) {
            throw MilestoneCompletionPreConditionException(
                "All tasks must be completed before completing milestone"
            )
        }
    }

    private fun validateStartPreconditions(milestone: Milestone) {
        if (!milestone.dependencies.all { it.isCompleted }) {
            throw MilestoneConstraintViolationException(
                "Cannot start milestone with incomplete dependencies"
            )
        }
    }

    private fun validateBlockedPreconditions(milestone: Milestone) {
        val hasBlockedDependency = milestone.dependencies.any { it.status == MilestoneStatus.BLOCKED }
        val hasIncompleteDependency = milestone.dependencies.any { !it.isCompleted }
        val hasUnassignedTasks = milestone.tasks.any { it.assignee == null }

        if (!hasBlockedDependency && !hasIncompleteDependency && !hasUnassignedTasks) {
            throw MilestoneConstraintViolationException(
                "Cannot block milestone without blocked/incomplete dependencies or unassigned tasks"
            )
        }
    }

    private fun hasCyclicDependency(
        milestone: Milestone,
        dependency: Milestone,
        visited: MutableSet<String>
    ): Boolean {
        if (!visited.add(milestone.id)) {
            return true
        }

        if (milestone.id == dependency.id) {
            return true
        }

        return milestone.dependencies.any {
            hasCyclicDependency(it, dependency, visited)
        }
    }
}
