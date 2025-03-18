package com.projecthub.domain.project

import com.projecthub.domain.ValueObject
import com.projecthub.domain.user.UserId
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

/**
 * Value object representing a project team member.
 */
@Embeddable
data class ProjectMember(
    @Column(name = "user_id")
    val userId: UserId,

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    val role: ProjectRole
) : ValueObject {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProjectMember) return false
        return userId == other.userId && role == other.role
    }

    override fun hashCode(): Int = 31 * userId.hashCode() + role.hashCode()

    override fun toString(): String = "ProjectMember(userId=$userId, role=$role)"
}
