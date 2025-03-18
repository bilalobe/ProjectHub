package com.projecthub.domain.project

import com.projecthub.domain.ValueObject
import jakarta.persistence.Embeddable

/**
 * Value object representing a team member's role in a project.
 */
@Embeddable
enum class ProjectRole : ValueObject {
    OWNER,
    MANAGER,
    MEMBER,
    VIEWER;

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProjectRole) return false
        return this.name == other.name
    }

    override fun hashCode(): Int = name.hashCode()

    override fun toString(): String = name
}
