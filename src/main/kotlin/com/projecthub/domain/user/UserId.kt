package com.projecthub.domain.user

import com.projecthub.domain.ValueObject
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.util.UUID

/**
 * Value object representing a strongly typed user ID.
 */
@Embeddable
data class UserId(
    @Column(name = "user_id")
    val value: UUID
) : ValueObject {
    companion object {
        fun create(): UserId = UserId(UUID.randomUUID())

        fun fromString(value: String): UserId {
            return try {
                UserId(UUID.fromString(value))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid UserId format: $value", e)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserId) return false
        return value == other.value
    }

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = value.toString()
}
