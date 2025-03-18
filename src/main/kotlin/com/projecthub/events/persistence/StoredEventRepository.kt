package com.projecthub.events.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

/**
 * Repository for accessing stored events in the database.
 */
@Repository
interface StoredEventRepository : JpaRepository<StoredEvent, UUID> {

    /**
     * Find all events for a specific aggregate.
     */
    fun findByAggregateId(aggregateId: UUID, sort: Sort): List<StoredEvent>

    /**
     * Find events by their type.
     */
    fun findByEventType(eventType: String, pageable: Pageable): Page<StoredEvent>

    /**
     * Find events that occurred after a specific time.
     */
    fun findByOccurredOnAfter(timestamp: Instant, pageable: Pageable): Page<StoredEvent>

    /**
     * Count events by aggregate ID.
     */
    fun countByAggregateId(aggregateId: UUID): Long

    /**
     * Find events by aggregate ID and type.
     */
    fun findByAggregateIdAndEventType(aggregateId: UUID, eventType: String, sort: Sort): List<StoredEvent>

    /**
     * Find the most recent events.
     */
    @Query("SELECT e FROM StoredEvent e ORDER BY e.occurredOn DESC")
    fun findRecentEvents(pageable: Pageable): Page<StoredEvent>
}
