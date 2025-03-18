package com.projecthub.events.persistence

import jakarta.persistence.*
import java.time.Instant
import java.util.*

/**
 * JPA entity for storing domain events in the database.
 * This enables event sourcing capabilities and event replay.
 */
@Entity
@Table(
    name = "stored_events", indexes = [
        Index(name = "idx_stored_events_aggregate_id", columnList = "aggregate_id"),
        Index(name = "idx_stored_events_event_type", columnList = "event_type"),
        Index(name = "idx_stored_events_occurred_on", columnList = "occurred_on")
    ]
)
class StoredEvent(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "aggregate_id", nullable = false)
    val aggregateId: UUID,

    @Column(name = "event_id", nullable = false)
    val eventId: UUID,

    @Column(name = "event_type", nullable = false)
    val eventType: String,

    @Column(name = "occurred_on", nullable = false)
    val occurredOn: Instant,

    @Column(name = "event_body", nullable = false, columnDefinition = "TEXT")
    val eventBody: String,

    @Column(name = "event_version", nullable = false)
    val eventVersion: Int = 1,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
)
