package com.projecthub.domain.milestone.validation

import com.projecthub.domain.validation.ValidationResult
import com.projecthub.domain.validation.Validator
import com.projecthub.domain.milestone.Milestone
import org.springframework.stereotype.Component

@Component
class MilestoneValidator : Validator<Milestone> {

    fun validateMilestones(milestones: Set<Milestone>): ValidationResult {
        return ValidationResult.Builder().apply {
            validateTimelineConsistency(milestones)
        }.build()
    }

    private fun ValidationResult.Builder.validateTimelineConsistency(milestones: Set<Milestone>) {
        milestones.forEach { milestone ->
            if (milestone.dependencies.isNotEmpty()) {
                milestone.dependencies.forEach { dependency ->
                    if (!dependency.dueDate.isBefore(milestone.dueDate)) {
                        addError(
                            "Milestone '${milestone.name}' due date must not be before its dependency '${dependency.name}' due date"
                        )
                    }
                }
            }
        }
    }

    fun validateCreate(milestone: Milestone): ValidationResult {
        return ValidationResult.Builder().apply {
            validateBasicFields(milestone)
            validateInitialStatus(milestone)
            validateInitialProgress(milestone)
            validateDates(milestone)
        }.build()
    }

    private fun ValidationResult.Builder.validateBasicFields(milestone: Milestone) {
        if (milestone.name.isBlank()) {
            addError("Milestone name is required")
        }
        if (milestone.dueDate == null) {
            addError("Due date is required")
        }
        if (milestone.project == null) {
            addError("Project association is required")
        }
    }

    private fun ValidationResult.Builder.validateInitialStatus(milestone: Milestone) {
        if (milestone.status != MilestoneStatus.PENDING) {
            addError("New milestones must start in PENDING status")
        }
    }

    private fun ValidationResult.Builder.validateInitialProgress(milestone: Milestone) {
        if (milestone.progress != 0) {
            addError("New milestone must have 0% progress")
        }
    }

    private fun ValidationResult.Builder.validateDates(milestone: Milestone) {
        val projectStart = milestone.project.startDate
        val projectEnd = milestone.project.dueDate

        if (milestone.dueDate.isBefore(projectStart)) {
            addError("Due date cannot be before project start date")
        }

        projectEnd?.let {
            if (milestone.dueDate.isAfter(it)) {
                addError("Due date cannot be after project end date")
            }
        }
    }

    fun validateDelete(milestone: Milestone): ValidationResult {
        return ValidationResult.Builder().apply {
            if (milestone.status == MilestoneStatus.IN_PROGRESS) {
                addError("Cannot delete in-progress milestone")
            }
            if (milestone.tasks.isNotEmpty()) {
                addError("Cannot delete milestone with existing tasks")
            }
            if (milestone.project.milestones.any { it.dependencies.contains(milestone) }) {
                addError("Cannot delete milestone that others depend on")
            }
        }.build()
    }
}
