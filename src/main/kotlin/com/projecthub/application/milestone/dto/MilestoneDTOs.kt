package com.projecthub.application.milestone.dto

import com.projecthub.domain.milestone.MilestoneStatus
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Data Transfer Object for Milestone entities
 */
data class MilestoneDTO(
    val id: String,
    val name: String,
    val description: String,
    val projectId: String,
    val dueDate: LocalDate,
    val status: MilestoneStatus,
    val progress: Int,
    val assigneeId: String?,
    val completionDate: LocalDate?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val dependencies: List<String> = emptyList() // IDs of dependencies
)

/**
 * Command for creating a new milestone
 */
data class CreateMilestoneCommand(
    val name: String,
    val description: String,
    val projectId: String,
    val dueDate: LocalDate,
    val assigneeId: String? = null,
    val dependencies: List<String> = emptyList() // IDs of dependencies
)

/**
 * Command for updating a milestone
 */
data class UpdateMilestoneCommand(
    val name: String,
    val description: String,
    val dueDate: LocalDate
)

/**
 * Command for updating milestone status
 */
data class UpdateMilestoneStatusCommand(
    val newStatus: MilestoneStatus
)

/**
 * Command for assigning a milestone
 */
data class AssignMilestoneCommand(
    val assigneeId: String
)

/**
 * Command for adding a dependency
 */
data class AddDependencyCommand(
    val dependencyId: String
)