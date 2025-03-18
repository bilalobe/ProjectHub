package com.projecthub.infrastructure.persistence.milestone.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface MilestoneJpaRepository : JpaRepository<MilestoneJpaEntity, String> {

    fun findByProjectId(projectId: String): List<MilestoneJpaEntity>

    fun findByAssigneeId(assigneeId: String): List<MilestoneJpaEntity>

    @Query("SELECT m FROM MilestoneJpaEntity m WHERE m.dueDate >= :date AND m.status <> 'COMPLETED'")
    fun findUpcomingMilestones(@Param("date") date: LocalDate): List<MilestoneJpaEntity>

    @Query("SELECT m FROM MilestoneJpaEntity m WHERE m.dueDate < :date AND m.status <> 'COMPLETED'")
    fun findOverdueMilestones(@Param("date") date: LocalDate): List<MilestoneJpaEntity>
}
