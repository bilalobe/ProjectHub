package com.projecthub.application.project.dto

import java.time.LocalDateTime

/**
 * Data Transfer Object for Project entities
 */
data class ProjectDTO(
    val id: String,
    val name: String,
    val description: String,
    val ownerId: String,
    val teamId: String?,
    val completionDate: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

/**
 * Command for creating a new project
 */
data class CreateProjectCommand(
    val name: String,
    val description: String,
    val ownerId: String
)

/**
 * Command for updating an existing project
 */
data class UpdateProjectCommand(
    val name: String,
    val description: String
)