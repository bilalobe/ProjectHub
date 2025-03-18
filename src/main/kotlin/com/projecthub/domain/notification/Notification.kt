package com.projecthub.domain.notification

import com.projecthub.domain.BaseEntity
import com.projecthub.domain.notification.event.NotificationEvent
import com.projecthub.domain.user.UserId
import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "notifications")
class Notification(
    @Column(nullable = false)
    var type: String,

    @Column(nullable = false, length = 1000)
    var content: String,

    @Column(name = "recipient_id", nullable = false)
    var recipientId: UserId,

    @Column(nullable = false)
    var isRead: Boolean = false,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "read_at")
    var readAt: Instant? = null,

    @Column(name = "expires_at")
    var expiresAt: Instant? = null
) : BaseEntity() {

    /**
     * Creates a new notification and emits a creation event
     */
    companion object {
        fun create(
            type: String,
            content: String,
            recipientId: UserId
        ): Notification {
            require(content.isNotBlank()) { "Content cannot be empty" }
            require(type.isNotBlank()) { "Type cannot be empty" }

            return Notification(
                type = type,
                content = content,
                recipientId = recipientId
            ).also { notification ->
                notification.registerEvent(
                    NotificationEvent.Created(
                        notificationId = notification.id,
                        recipientId = recipientId.value,
                        type = type,
                        content = content
                    )
                )
            }
        }
    }

    /**
     * Marks the notification as read
     */
    fun markAsRead() {
        if (!isRead) {
            isRead = true
            readAt = Instant.now()
            registerEvent(
                NotificationEvent.Read(
                    notificationId = id,
                    recipientId = recipientId.value
                )
            )
        }
    }

    /**
     * Sets the expiration time for the notification
     */
    fun setExpiration(expiresAt: Instant) {
        require(expiresAt.isAfter(Instant.now())) { "Expiration time must be in the future" }
        this.expiresAt = expiresAt
    }

    /**
     * Checks if the notification has expired
     */
    fun isExpired(): Boolean =
        expiresAt?.let { it.isBefore(Instant.now()) } ?: false

    /**
     * Deletes the notification and emits a deletion event
     */
    fun delete() {
        registerEvent(
            NotificationEvent.Deleted(
                notificationId = id,
                recipientId = recipientId.value
            )
        )
    }
}
