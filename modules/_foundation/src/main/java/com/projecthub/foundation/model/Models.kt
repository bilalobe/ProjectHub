package com.projecthub.model

data class Project(
    val id: String,
    val name: String,
    val description: String,
    val status: ProjectStatus,
    val teamId: String,
    val createdAt: Long,
    val updatedAt: Long
)

enum class ProjectStatus {
    PLANNING,
    IN_PROGRESS,
    REVIEW,
    COMPLETED
}

data class Team(
    val id: String,
    val name: String,
    val description: String,
    val memberIds: List<String>,
    val leaderId: String
)

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val avatarUrl: String?
)

enum class UserRole {
    ADMIN,
    MANAGER,
    MEMBER
}