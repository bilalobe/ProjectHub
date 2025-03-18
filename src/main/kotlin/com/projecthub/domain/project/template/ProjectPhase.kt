package com.projecthub.domain.project.template

import com.projecthub.domain.ValueObject
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

/**
 * Represents a phase within a project template.
 * Phases define the sequential steps and deliverables that make up a project.
 */
@Embeddable
data class ProjectPhase(
    @Column(nullable = false)
    val name: String,

    @Column
    val description: String? = null,

    @Column(name = "order_index", nullable = false)
    val orderIndex: Int,

    @Column(name = "estimated_duration_days", nullable = false)
    val estimatedDurationDays: Int,

    @Column(nullable = false)
    val isRequired: Boolean = true,

    @Column
    val deliverables: String? = null
) : ValueObject {
    init {
        require(name.isNotBlank()) { "Phase name cannot be blank" }
        require(orderIndex >= 0) { "Phase order index must be non-negative" }
        require(estimatedDurationDays > 0) { "Phase duration must be positive" }
    }
}
