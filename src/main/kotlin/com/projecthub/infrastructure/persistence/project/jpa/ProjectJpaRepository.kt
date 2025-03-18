package com.projecthub.infrastructure.persistence.project.jpa

import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data JPA repository for Project entities.
 * This is a pure infrastructure concern and has no knowledge of our domain models.
 */
@Repository
interface ProjectJpaRepository : JpaRepository<ProjectJpaEntity, UUID> {

    /**
     * Finds all projects with the specified status.
     *
     * @param status The status to filter by
     * @return A list of project JPA entities with the given status
     */
    fun findByStatus(status: ProjectJpaEntity.ProjectStatusJpa): List<ProjectJpaEntity>

    /**
     * Finds all projects owned by the specified user.
     *
     * @param ownerId The ID of the owner to filter by
     * @return A list of project JPA entities with the given owner
     */
    fun findByOwnerId(ownerId: UUID): List<ProjectJpaEntity>
}
