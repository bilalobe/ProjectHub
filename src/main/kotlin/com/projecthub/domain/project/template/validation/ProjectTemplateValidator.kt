package com.projecthub.domain.project.template.validation

import com.projecthub.domain.validation.ValidationResult
import com.projecthub.domain.validation.Validator
import com.projecthub.domain.project.template.ProjectTemplate

class ProjectTemplateValidator : Validator<ProjectTemplate> {

    fun validate(template: ProjectTemplate): ValidationResult {
        return ValidationResult.Builder().apply {
            validateBasicProperties(template)
            validatePhases(template)
        }.build()
    }

    private fun ValidationResult.Builder.validateBasicProperties(template: ProjectTemplate) {
        if (template.name.isBlank()) {
            addError("Template name cannot be blank")
        }

        if (template.estimatedDurationDays <= 0) {
            addError("Estimated duration must be positive")
        }
    }

    private fun ValidationResult.Builder.validatePhases(template: ProjectTemplate) {
        // Check if phases are ordered correctly
        val phases = template.phases.sortedBy { it.order }
        phases.zipWithNext().forEach { (current, next) ->
            if (next.order != current.order + 1) {
                addError("Phase order must be sequential")
            }
        }

        // Check phase durations
        val totalPhaseDuration = phases.sumOf { it.estimatedDurationDays }
        if (totalPhaseDuration > template.estimatedDurationDays) {
            addError("Sum of phase durations cannot exceed template duration")
        }

        // Check for duplicate phase names
        val phaseNames = phases.map { it.name }
        if (phaseNames.size != phaseNames.distinct().size) {
            addError("Phase names must be unique within a template")
        }
    }
}