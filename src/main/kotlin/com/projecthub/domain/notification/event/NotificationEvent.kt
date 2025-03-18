package com.projecthub.domain.notification.event

import com.projecthub.events.DomainEvent
import java.time.Instant
import java.util.UUID

/**
 * Sealed class representing all possible notification events.
 */
sealed class NotificationEvent : DomainEvent() {
    abstract val notificationId: UUID
    abstract val recipientId: UUID

    data class Created(
        override val notificationId: UUID,
        override val recipientId: UUID,
        val type: String,
        val content: String,
        override val occurredAt: Instant = Instant.now()
    ) : NotificationEvent()

    data class Read(
        override val notificationId: UUID,
        override val recipientId: UUID,
        override val occurredAt: Instant = Instant.now()
    ) : NotificationEvent()

    data class Deleted(
        override val notificationId: UUID,
        override val recipientId: UUID,
        override val occurredAt: Instant = Instant.now()
    ) : NotificationEvent()
}
