package com.projecthub.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant
import java.util.*

@MappedSuperclass
abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID()

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: Instant = Instant.now()
        private set

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: Instant = Instant.now()
        private set

    @Version
    var version: Long = 0
        private set

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseEntity) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
