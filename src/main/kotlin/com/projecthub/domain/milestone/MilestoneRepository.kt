package com.projecthub.domain.milestone

import java.time.LocalDate

/**
 * Repository interface for milestones (outbound port)
 * This interface defines the persistence operations available for Milestone entities
 */
interface MilestoneRepository {
    fun findById(id: String): Milestone?
    fun findAll(): List<Milestone>
    fun findByProjectId(projectId: String): List<Milestone>
    fun findByAssigneeId(assigneeId: String): List<Milestone>
    fun findUpcomingMilestones(date: LocalDate): List<Milestone>
    fun findOverdueMilestones(date: LocalDate): List<Milestone>
    fun save(milestone: Milestone): Milestone
    fun deleteById(id: String)
}