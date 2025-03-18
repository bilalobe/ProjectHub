package com.projecthub.application.project.domain

import java.time.LocalDateTime

/**
 * Core domain entity representing a project
 * Technology-agnostic with no infrastructure dependencies
 */
class Project private constructor(
    val id: String,
    private var _name: String,
    private var _description: String,
    private var _ownerId: String,
    private var _teamId: String,
    private var _status: ProjectStatus,
    private var _completionDate: LocalDateTime?,
    val createdAt: LocalDateTime,
    private var _updatedAt: LocalDateTime
) {
    // Getters to expose immutable view of properties
    val name: String get() = _name
    val description: String get() = _description
    val ownerId: String get() = _ownerId
    val teamId: String get() = _teamId
    val status: ProjectStatus get() = _status
    val completionDate: LocalDateTime? get() = _completionDate
    val updatedAt: LocalDateTime get() = _updatedAt
    
    // Domain methods that enforce business rules
    fun updateDetails(name: String?, description: String?) {
        name?.let { _name = it }
        description?.let { _description = it }
        _updatedAt = LocalDateTime.now()
    }
    
    fun assignTeam(teamId: String) {
        _teamId = teamId
        _updatedAt = LocalDateTime.now()
    }
    
    fun changeOwner(ownerId: String) {
        _ownerId = ownerId
        _updatedAt = LocalDateTime.now()
    }
    
    fun updateStatus(status: ProjectStatus) {
        _status = status
        _updatedAt = LocalDateTime.now()
    }
    
    fun setCompletionDate(date: LocalDateTime?) {
        _completionDate = date
        _updatedAt = LocalDateTime.now()
    }
    
    companion object {
        // Factory method for creating new projects
        fun create(
            id: String,
            name: String,
            description: String,
            ownerId: String,
            teamId: String
        ): Project {
            val now = LocalDateTime.now()
            return Project(
                id = id,
                _name = name,
                _description = description,
                _ownerId = ownerId,
                _teamId = teamId,
                _status = ProjectStatus.PLANNING,
                _completionDate = null,
                createdAt = now,
                _updatedAt = now
            )
        }
        
        // Reconstitution method for repositories
        fun reconstitute(
            id: String,
            name: String,
            description: String,
            ownerId: String,
            teamId: String,
            status: ProjectStatus,
            completionDate: LocalDateTime?,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): Project {
            return Project(
                id, name, description, ownerId, teamId,
                status, completionDate, createdAt, updatedAt
            )
        }
    }
}
