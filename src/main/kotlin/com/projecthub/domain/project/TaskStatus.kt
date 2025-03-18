package com.projecthub.domain.project

import com.projecthub.domain.ValueObject
import jakarta.persistence.Embeddable

/**
 * Value object representing the status of a task.
 */
@Embeddable
enum class TaskStatus : ValueObject {
    TODO,
    IN_PROGRESS,
    BLOCKED,
    REVIEW,
    DONE;

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TaskStatus) return false
        return this.name == other.name
    }

    override fun hashCode(): Int = name.hashCode()

    override fun toString(): String = name
}
