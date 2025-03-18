package com.projecthub.domain.project

import com.projecthub.domain.AggregateRoot
import com.projecthub.domain.project.event.ProjectCreatedEvent
import com.projecthub.domain.project.event.ProjectUpdatedEvent
import com.projecthub.domain.project.event.ProjectAssignedEvent
import com.projecthub.domain.project.event.ProjectCompletedEvent
import java.time.LocalDateTime

/**
 * Project aggregate root entity
 * Represents a project in the system with all its business rules and behaviors
 */
class Project private constructor(
    val id: String,
    var name: String,
    var description: String,
    var ownerId: String,
    var teamId: String? = null,
    var completionDate: LocalDateTime? = null,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now()
) : AggregateRoot<String>() {

    override fun getId(): String = id

    /**
     * Updates project details and registers a domain event
     */
    fun updateDetails(name: String, description: String) {
        this.name = name
        this.description = description
        this.updatedAt = LocalDateTime.now()

        registerEvent(ProjectUpdatedEvent(id, name, description))
    }

    /**
     * Assigns project to a team and registers a domain event
     */
    fun assignToTeam(teamId: String) {
        this.teamId = teamId
        this.updatedAt = LocalDateTime.now()

        registerEvent(ProjectAssignedEvent(id, teamId))
    }

    /**
     * Marks project as completed and registers a domain event
     */
    fun complete() {
        val now = LocalDateTime.now()
        this.completionDate = now
        this.updatedAt = now

        registerEvent(ProjectCompletedEvent(id, now.toString()))
    }

    companion object {
        /**
         * Creates a new Project instance and registers a project created event
         */
        fun create(id: String, name: String, description: String, ownerId: String): Project {
            val project = Project(id, name, description, ownerId)

            // Register domain event for new project creation
            project.registerEvent(ProjectCreatedEvent(id, name, description, ownerId))

            return project
        }
    }
}
