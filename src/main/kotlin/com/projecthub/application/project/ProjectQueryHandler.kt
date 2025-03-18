package com.projecthub.application.project

import com.projecthub.application.cqrs.Query
import com.projecthub.application.cqrs.QueryHandler
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Handler for project-related queries.
 * Uses coroutines to execute queries asynchronously.
 */
@Component
class ProjectQueryHandler(
    private val projectApplicationService: ProjectApplicationService
) {
    /**
     * Query handler to get a project by ID.
     */
    @Component
    class GetProjectByIdHandler(
        private val projectApplicationService: ProjectApplicationService
    ) : QueryHandler<GetProjectByIdQuery, ProjectDto> {

        override suspend fun handle(query: GetProjectByIdQuery): ProjectDto {
            return projectApplicationService.getProject(query.projectId)
        }
    }

    /**
     * Query handler to get projects by owner ID.
     */
    @Component
    class GetProjectsByOwnerHandler(
        private val projectApplicationService: ProjectApplicationService
    ) : QueryHandler<GetProjectsByOwnerQuery, List<ProjectDto>> {

        override suspend fun handle(query: GetProjectsByOwnerQuery): List<ProjectDto> {
            return projectApplicationService.findProjectsByOwner(query.ownerId)
        }
    }

    /**
     * Query handler to get projects by status.
     */
    @Component
    class GetProjectsByStatusHandler(
        private val projectApplicationService: ProjectApplicationService
    ) : QueryHandler<GetProjectsByStatusQuery, List<ProjectDto>> {

        override suspend fun handle(query: GetProjectsByStatusQuery): List<ProjectDto> {
            return projectApplicationService.findProjectsByStatus(query.status)
        }
    }
}

/**
 * Query to get a project by ID.
 */
data class GetProjectByIdQuery(val projectId: UUID) : Query<ProjectDto> {
    override val queryName: String = "GetProjectByIdQuery"
}

/**
 * Query to get projects by owner ID.
 */
data class GetProjectsByOwnerQuery(val ownerId: UUID) : Query<List<ProjectDto>> {
    override val queryName: String = "GetProjectsByOwnerQuery"
}

/**
 * Query to get projects by status.
 */
data class GetProjectsByStatusQuery(val status: String) : Query<List<ProjectDto>> {
    override val queryName: String = "GetProjectsByStatusQuery"
}
