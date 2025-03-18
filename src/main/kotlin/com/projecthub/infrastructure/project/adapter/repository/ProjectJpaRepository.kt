package com.projecthub.infrastructure.project.adapter.repository

import com.projecthub.application.project.domain.ProjectStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectJpaRepository : JpaRepository<ProjectJpaEntity, String> {
    fun findByOwnerId(ownerId: String): List<ProjectJpaEntity>
    fun findByTeamId(teamId: String): List<ProjectJpaEntity>
    fun findByStatus(status: ProjectStatus): List<ProjectJpaEntity>
    fun findByStatusAndOwnerId(status: ProjectStatus, ownerId: String): List<ProjectJpaEntity>
}
