package com.projecthub.application.project

import com.projecthub.application.cqrs.Command
import com.projecthub.application.cqrs.CommandHandler
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Handlers for project-related commands.
 * Uses coroutines to execute commands asynchronously.
 */
@Component
class ProjectCommandHandler {
    /**
     * Command handler for creating a project.
     */
    @Component
    class CreateProjectHandler(
        private val projectApplicationService: ProjectApplicationService
    ) : CommandHandler<CreateProjectCommand, UUID> {

        override suspend fun handle(command: CreateProjectCommand): UUID {
            return projectApplicationService.createProject(command)
        }
    }

    /**
     * Command handler for updating a project.
     */
    @Component
    class UpdateProjectHandler(
        private val projectApplicationService: ProjectApplicationService
    ) : CommandHandler<UpdateProjectCommand, Unit> {

        override suspend fun handle(command: UpdateProjectCommand) {
            projectApplicationService.updateProject(command)
        }
    }

    /**
     * Command handler for completing a project.
     */
    @Component
    class CompleteProjectHandler(
        private val projectApplicationService: ProjectApplicationService
    ) : CommandHandler<CompleteProjectCommand, Unit> {

        override suspend fun handle(command: CompleteProjectCommand) {
            projectApplicationService.completeProject(command.projectId)
        }
    }
}

/**
 * Command for completing a project.
 */
data class CompleteProjectCommand(val projectId: UUID) : Command<Unit> {
    override val commandName: String = "CompleteProjectCommand"
}
