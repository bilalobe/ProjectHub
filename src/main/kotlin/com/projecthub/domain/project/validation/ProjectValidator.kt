package com.projecthub.domain.project.validation

import com.projecthub.domain.validation.ValidationResult
import com.projecthub.domain.validation.Validator
import com.projecthub.domain.project.Project
import com.projecthub.domain.project.ProjectRepository
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Domain validator for Project entities.
 * Implements core business rules for project validation including:
 * - Project name uniqueness within a team
 * - Date validations (start date before due date, due date not in past)
 */
class ProjectValidator(
    private val projectRepository: ProjectRepository
) : Validator<Project> {

    /**
     * Validates a project for creation
     */
    suspend fun validateCreate(project: Project): ValidationResult {
        return ValidationResult.Builder().apply {
            validateName(project.name, project.ownerId.value)
            validateDates(project.startDate, project.dueDate)
        }.build()
    }

    /**
     * Validates a project for update, allowing same name for same project
     */
    suspend fun validateUpdate(projectId: String, project: Project): ValidationResult {
        return ValidationResult.Builder().apply {
            validateNameForUpdate(projectId, project.name, project.ownerId.value)
            validateDates(project.startDate, project.dueDate)
        }.build()
    }

    private suspend fun ValidationResult.Builder.validateName(name: String, ownerId: String) {
        if (projectRepository.existsByNameAndTeamId(name, ownerId)) {
            addError("Project with this name already exists for the team")
        }
    }

    private suspend fun ValidationResult.Builder.validateNameForUpdate(
        projectId: String,
        name: String,
        ownerId: String
    ) {
        if (projectRepository.existsByNameAndTeamId(name, ownerId)) {
            projectRepository.findById(projectId)?.let { existingProject ->
                if (existingProject.name != name) {
                    addError("Project with this name already exists for the team")
                }
            }
        }
    }

    private fun ValidationResult.Builder.validateDates(
        startDate: Instant,
        dueDate: Instant?
    ) {
        dueDate?.let { deadline ->
            if (deadline.isBefore(startDate)) {
                addError("Due date cannot be before start date")
            }

            // Truncate to days for date-only comparison
            val today = Instant.now().truncatedTo(ChronoUnit.DAYS)
            val dueDateTruncated = deadline.truncatedTo(ChronoUnit.DAYS)

            if (dueDateTruncated.isBefore(today)) {
                addError("Due date cannot be in the past")
            }
        }
    }
}
