package com.projecthub.application.milestone.port.`in`

import com.projecthub.application.milestone.dto.*
import java.time.LocalDate

/**
 * Inbound port for milestone management functionality
 * This interface defines the use cases available for managing milestones
 */
interface MilestoneManagementUseCase {
    /**
     * Creates a new milestone
     */
    fun createMilestone(command: CreateMilestoneCommand): MilestoneDTO

    /**
     * Updates milestone details
     */
    fun updateMilestone(milestoneId: String, command: UpdateMilestoneCommand): MilestoneDTO

    /**
     * Updates milestone status
     */
    fun updateMilestoneStatus(milestoneId: String, command: UpdateMilestoneStatusCommand): MilestoneDTO

    /**
     * Assigns a milestone to a team member
     */
    fun assignMilestone(milestoneId: String, command: AssignMilestoneCommand): MilestoneDTO

    /**
     * Adds a dependency to a milestone
     */
    fun addDependency(milestoneId: String, command: AddDependencyCommand): MilestoneDTO

    /**
     * Gets a milestone by ID
     */
    fun getMilestoneById(milestoneId: String): MilestoneDTO?

    /**
     * Gets all milestones for a project
     */
    fun getProjectMilestones(projectId: String): List<MilestoneDTO>

    /**
     * Gets all milestones assigned to a specific person
     */
    fun getAssignedMilestones(assigneeId: String): List<MilestoneDTO>

    /**
     * Gets upcoming milestones
     */
    fun getUpcomingMilestones(date: LocalDate = LocalDate.now()): List<MilestoneDTO>

    /**
     * Gets overdue milestones
     */
    fun getOverdueMilestones(date: LocalDate = LocalDate.now()): List<MilestoneDTO>
}
