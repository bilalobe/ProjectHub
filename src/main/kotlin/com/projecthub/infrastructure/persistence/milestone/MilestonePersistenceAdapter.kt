package com.projecthub.infrastructure.persistence.milestone

import com.projecthub.application.milestone.mapper.MilestoneMapper
import com.projecthub.domain.milestone.Milestone
import com.projecthub.domain.milestone.MilestoneRepository
import com.projecthub.infrastructure.persistence.milestone.jpa.MilestoneJpaRepository
import com.projecthub.infrastructure.persistence.project.jpa.ProjectJpaRepository
import com.projecthub.infrastructure.persistence.user.jpa.UserJpaRepository
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class MilestonePersistenceAdapter(
    private val milestoneJpaRepository: MilestoneJpaRepository,
    private val projectJpaRepository: ProjectJpaRepository,
    private val userJpaRepository: UserJpaRepository,
    private val milestoneMapper: MilestoneMapper
) : MilestoneRepository {

    override fun findById(id: String): Milestone? {
        return milestoneJpaRepository.findById(id)
            .map { enrichAndMapToDomain(it) }
            .orElse(null)
    }

    override fun findAll(): List<Milestone> {
        return milestoneJpaRepository.findAll()
            .map { enrichAndMapToDomain(it) }
    }

    override fun findByProjectId(projectId: String): List<Milestone> {
        return milestoneJpaRepository.findByProjectId(projectId)
            .map { enrichAndMapToDomain(it) }
    }

    override fun findByAssigneeId(assigneeId: String): List<Milestone> {
        return milestoneJpaRepository.findByAssigneeId(assigneeId)
            .map { enrichAndMapToDomain(it) }
    }

    override fun findUpcomingMilestones(date: LocalDate): List<Milestone> {
        return milestoneJpaRepository.findUpcomingMilestones(date)
            .map { enrichAndMapToDomain(it) }
    }

    override fun findOverdueMilestones(date: LocalDate): List<Milestone> {
        return milestoneJpaRepository.findOverdueMilestones(date)
            .map { enrichAndMapToDomain(it) }
    }

    override fun save(milestone: Milestone): Milestone {
        val jpaEntity = milestoneMapper.toJpaEntity(milestone)
        val savedEntity = milestoneJpaRepository.save(jpaEntity)
        return enrichAndMapToDomain(savedEntity)
    }

    override fun deleteById(id: String) {
        milestoneJpaRepository.deleteById(id)
    }

    /**
     * Helper method to enrich JPA entity with associated entities before mapping to domain
     */
    private fun enrichAndMapToDomain(entity: com.projecthub.infrastructure.persistence.milestone.jpa.MilestoneJpaEntity): Milestone {
        // First map to domain entity
        val milestone = milestoneMapper.toDomainEntity(entity)

        // Then enrich with associated entities
        projectJpaRepository.findById(entity.projectId).ifPresent { projectEntity ->
            milestone.project = projectEntity.toDomainEntity()
        }

        entity.assigneeId?.let { assigneeId ->
            userJpaRepository.findById(assigneeId).ifPresent { userEntity ->
                milestone.assignee = userEntity.toDomainEntity()
            }
        }

        // Load dependencies if any
        entity.dependencies.forEach { depEntity ->
            milestone.addDependency(milestoneMapper.toDomainEntity(depEntity))
        }

        return milestone
    }
}
