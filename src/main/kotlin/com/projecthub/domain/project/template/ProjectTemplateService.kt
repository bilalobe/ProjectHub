package com.projecthub.domain.project.template

import com.projecthub.domain.project.Project
import com.projecthub.domain.project.ProjectFactory
import com.projecthub.domain.user.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

/**
 * Service for managing project templates and applying them to create projects.
 */
@Service
class ProjectTemplateService(
    private val projectTemplateRepository: ProjectTemplateRepository,
    private val projectFactory: ProjectFactory
) {

    /**
     * Retrieves all active project templates.
     *
     * @return A list of active project templates
     */
    @Transactional(readOnly = true)
    suspend fun getActiveTemplates(): List<ProjectTemplate> {
        return projectTemplateRepository.findByIsActiveTrue()
    }

    /**
     * Retrieves project templates by category.
     *
     * @param category The category to filter by
     * @return A list of project templates in the specified category
     */
    @Transactional(readOnly = true)
    suspend fun getTemplatesByCategory(category: ProjectCategory): List<ProjectTemplate> {
        return projectTemplateRepository.findByCategoryAndIsActiveTrue(category)
    }

    /**
     * Creates a new project template.
     *
     * @param name The name of the template
     * @param description The description of the template
     * @param estimatedDurationDays The estimated duration in days
     * @param category The category of the template
     * @return The created project template
     */
    @Transactional
    suspend fun createTemplate(
        name: String,
        description: String?,
        estimatedDurationDays: Int,
        category: ProjectCategory? = null
    ): ProjectTemplate {
        require(name.isNotBlank()) { "Template name cannot be blank" }
        require(estimatedDurationDays > 0) { "Duration must be positive" }

        val template = ProjectTemplate(
            name = name,
            description = description,
            estimatedDurationDays = estimatedDurationDays,
            category = category
        )

        return projectTemplateRepository.save(template)
    }

    /**
     * Applies a template to create a new project.
     *
     * @param templateId The ID of the template to apply
     * @param projectName The name of the project to create
     * @param ownerId The ID of the project owner
     * @param startDate The start date of the project
     * @return The created project
     */
    @Transactional
    suspend fun applyTemplate(
        templateId: String,
        projectName: String,
        ownerId: UserId,
        startDate: LocalDate = LocalDate.now()
    ): Project {
        val template = projectTemplateRepository.findById(templateId)
            ?: throw IllegalArgumentException("Template not found with ID: $templateId")

        require(template.isActive) { "Cannot use inactive template" }

        // Calculate end date based on template's estimated duration
        val endDate = startDate.plusDays(template.estimatedDurationDays.toLong())

        // Create the project using the factory
        val project = projectFactory.createProject(
            name = projectName,
            ownerId = ownerId,
            startDate = startDate,
            dueDate = endDate
        )

        // Apply template phases to create milestones
        template.phases.sortedBy { it.orderIndex }.forEach { phase ->
            val milestoneDueDate = startDate.plusDays(phase.estimatedDurationDays.toLong())
            projectFactory.addMilestone(
                project = project,
                name = phase.name,
                dueDate = milestoneDueDate,
                description = phase.description
            )
        }

        return project
    }

    /**
     * Updates an existing project template.
     *
     * @param templateId The ID of the template to update
     * @param name The new name (optional)
     * @param description The new description (optional)
     * @param estimatedDurationDays The new estimated duration (optional)
     * @param category The new category (optional)
     * @return The updated project template
     */
    @Transactional
    suspend fun updateTemplate(
        templateId: String,
        name: String? = null,
        description: String? = null,
        estimatedDurationDays: Int? = null,
        category: ProjectCategory? = null
    ): ProjectTemplate {
        val template = projectTemplateRepository.findById(templateId)
            ?: throw IllegalArgumentException("Template not found with ID: $templateId")

        name?.let {
            require(it.isNotBlank()) { "Template name cannot be blank" }
            template.name = it
        }

        description?.let { template.description = it }

        estimatedDurationDays?.let {
            require(it > 0) { "Duration must be positive" }
            template.estimatedDurationDays = it
        }

        category?.let { template.category = it }

        return projectTemplateRepository.save(template)
    }

    /**
     * Deactivates a project template.
     *
     * @param templateId The ID of the template to deactivate
     */
    @Transactional
    suspend fun deactivateTemplate(templateId: String) {
        val template = projectTemplateRepository.findById(templateId)
            ?: throw IllegalArgumentException("Template not found with ID: $templateId")

        template.isActive = false
        projectTemplateRepository.save(template)
    }

    /**
     * Adds a phase to an existing template.
     *
     * @param templateId The ID of the template to add the phase to
     * @param phase The phase to add
     * @return The updated project template
     */
    @Transactional
    suspend fun addPhaseToTemplate(
        templateId: String,
        phase: ProjectPhase
    ): ProjectTemplate {
        val template = projectTemplateRepository.findById(templateId)
            ?: throw IllegalArgumentException("Template not found with ID: $templateId")

        template.addPhase(phase)
        return projectTemplateRepository.save(template)
    }
}