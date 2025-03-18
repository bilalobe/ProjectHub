package com.projecthub.domain.project.template

/**
 * Repository interface for Project Templates
 */
interface ProjectTemplateRepository {

    /**
     * Saves a project template.
     */
    suspend fun save(template: ProjectTemplate): ProjectTemplate

    /**
     * Finds a project template by ID.
     */
    suspend fun findById(id: String): ProjectTemplate?

    /**
     * Finds all active project templates.
     */
    suspend fun findByIsActiveTrue(): List<ProjectTemplate>

    /**
     * Finds active project templates by category.
     */
    suspend fun findByCategoryAndIsActiveTrue(category: ProjectCategory): List<ProjectTemplate>

    /**
     * Finds project templates by name (partial match).
     */
    suspend fun findByNameContainingIgnoreCase(name: String): List<ProjectTemplate>

    /**
     * Deletes a project template.
     */
    suspend fun delete(template: ProjectTemplate)
}