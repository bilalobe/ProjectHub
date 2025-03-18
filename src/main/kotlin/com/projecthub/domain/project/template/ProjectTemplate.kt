package com.projecthub.domain.project.template

import com.projecthub.domain.BaseEntity
import jakarta.persistence.*

/**
 * Represents a project template that can be used to create standardized projects.
 * Templates define phases, components, and estimated duration for projects.
 */
@Entity
@Table(name = "project_templates")
class ProjectTemplate(
    @Column(nullable = false)
    var name: String,

    @Column(length = 1000)
    var description: String? = null,

    @ElementCollection
    @CollectionTable(
        name = "project_template_phases",
        joinColumns = [JoinColumn(name = "template_id")]
    )
    var phases: MutableSet<ProjectPhase> = mutableSetOf(),

    @ElementCollection
    @CollectionTable(
        name = "project_template_components",
        joinColumns = [JoinColumn(name = "template_id")]
    )
    @Column(name = "component")
    var requiredComponents: MutableSet<String> = mutableSetOf(),

    @Column(name = "estimated_duration_days", nullable = false)
    var estimatedDurationDays: Int,

    @Column(nullable = false)
    var isActive: Boolean = true,

    @Column
    @Enumerated(EnumType.STRING)
    var category: ProjectCategory? = null
) : BaseEntity() {

    /**
     * Creates a copy of this template, which can be modified without affecting the original.
     *
     * @return A new ProjectTemplate instance with the same properties
     */
    fun copy(): ProjectTemplate {
        return ProjectTemplate(
            name = this.name,
            description = this.description,
            phases = this.phases.toMutableSet(),
            requiredComponents = this.requiredComponents.toMutableSet(),
            estimatedDurationDays = this.estimatedDurationDays,
            isActive = this.isActive,
            category = this.category
        )
    }

    /**
     * Adds a phase to the project template.
     *
     * @param phase The phase to add
     */
    fun addPhase(phase: ProjectPhase) {
        phases.add(phase)
    }

    /**
     * Adds a required component to the project template.
     *
     * @param component The component to add
     */
    fun addRequiredComponent(component: String) {
        requiredComponents.add(component)
    }

    /**
     * Changes the active status of the template.
     *
     * @param status The new active status
     */
    fun setActiveStatus(status: Boolean) {
        isActive = status
    }
}

/**
 * Categories for project templates
 */
enum class ProjectCategory {
    RESEARCH,
    DEVELOPMENT,
    DESIGN,
    DOCUMENTATION,
    ANALYSIS,
    TESTING
}