package com.projecthub.application.project

import com.projecthub.application.BaseApplicationService
import com.projecthub.application.CoroutineTransactionManager
import com.projecthub.application.exceptions.EntityNotFoundException
import com.projecthub.domain.project.Project
import com.projecthub.domain.project.ProjectRepository
import com.projecthub.domain.project.ProjectStatus
import com.projecthub.domain.user.UserId
import com.projecthub.events.ApplicationEventDispatcher
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

/**
 * Application service for project-related use cases.
 * Uses coroutines for asynchronous execution.
 */
@Service
class ProjectApplicationService(
    transactionManager: CoroutineTransactionManager,
    eventDispatcher: ApplicationEventDispatcher,
    private val projectRepository: ProjectRepository
) : BaseApplicationService(transactionManager, eventDispatcher) {

    /**
     * Creates a new project and dispatches the corresponding events.
     */
    suspend fun createProject(command: CreateProjectCommand): UUID = executeUseCase {
        logger.info("Creating project: ${command.name}")

        val ownerId = UserId(command.ownerId)

        val project = Project.create(
            name = command.name,
            description = command.description,
            startDate = command.startDate,
            dueDate = command.dueDate,
            ownerId = ownerId
        )

        val savedProject = projectRepository.save(project)
        dispatchEventsFrom(savedProject)

        savedProject.id
    }

    /**
     * Updates an existing project with new details.
     */
    suspend fun updateProject(command: UpdateProjectCommand): Unit = executeUseCase {
        logger.info("Updating project with ID: ${command.projectId}")

        val project = projectRepository.findById(command.projectId)
            ?: throw EntityNotFoundException("Project", command.projectId)

        project.update(
            name = command.name,
            description = command.description,
            startDate = command.startDate,
            dueDate = command.dueDate
        )

        val savedProject = projectRepository.save(project)
        dispatchEventsFrom(savedProject)
    }

    /**
     * Marks a project as completed.
     */
    suspend fun completeProject(projectId: UUID): Unit = executeUseCase {
        logger.info("Completing project with ID: $projectId")

        val project = projectRepository.findById(projectId)
            ?: throw EntityNotFoundException("Project", projectId)

        project.complete()

        val savedProject = projectRepository.save(project)
        dispatchEventsFrom(savedProject)
    }

    /**
     * Gets a project by ID.
     * Uses a read-only transaction.
     */
    suspend fun getProject(projectId: UUID): ProjectDto = executeReadOnly {
        val project = projectRepository.findById(projectId)
            ?: throw EntityNotFoundException("Project", projectId)

        ProjectDto(
            id = project.id,
            name = project.name,
            description = project.description,
            status = project.status.name,
            startDate = project.startDate,
            dueDate = project.dueDate,
            ownerId = project.ownerId.value
        )
    }

    /**
     * Finds all projects owned by a user.
     * Uses a read-only transaction.
     */
    suspend fun findProjectsByOwner(ownerId: UUID): List<ProjectDto> = executeReadOnly {
        val userId = UserId(ownerId)
        val projects = projectRepository.findByOwnerId(userId)

        projects.map { project ->
            ProjectDto(
                id = project.id,
                name = project.name,
                description = project.description,
                status = project.status.name,
                startDate = project.startDate,
                dueDate = project.dueDate,
                ownerId = project.ownerId.value
            )
        }
    }

    /**
     * Finds all projects with a specific status.
     * Uses a read-only transaction.
     */
    suspend fun findProjectsByStatus(status: String): List<ProjectDto> = executeReadOnly {
        try {
            val projectStatus = ProjectStatus.valueOf(status)
            val projects = projectRepository.findByStatus(projectStatus)

            projects.map { project ->
                ProjectDto(
                    id = project.id,
                    name = project.name,
                    description = project.description,
                    status = project.status.name,
                    startDate = project.startDate,
                    dueDate = project.dueDate,
                    ownerId = project.ownerId.value
                )
            }
        } catch (e: IllegalArgumentException) {
            logger.warn("Invalid project status: $status")
            emptyList()
        }
    }
}

/**
 * Data transfer object for Project.
 */
data class ProjectDto(
    val id: UUID,
    val name: String,
    val description: String,
    val status: String,
    val startDate: Instant,
    val dueDate: Instant?,
    val ownerId: UUID
)

/**
 * Command for creating a new project.
 */
data class CreateProjectCommand(
    val name: String,
    val description: String,
    val startDate: Instant,
    val dueDate: Instant?,
    val ownerId: UUID
)

/**
 * Command for updating an existing project.
 */
data class UpdateProjectCommand(
    val projectId: UUID,
    val name: String? = null,
    val description: String? = null,
    val startDate: Instant? = null,
    val dueDate: Instant? = null
)
