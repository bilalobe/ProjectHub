package com.projecthub.events.persistence

import com.projecthub.events.DomainEvent
import com.projecthub.events.EventStore
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * JPA-based implementation of the EventStore interface.
 */
@Component
class JpaEventStore(
    private val storedEventRepository: StoredEventRepository,
    private val eventSerializer: EventSerializer,
    private val eventClassResolver: EventClassResolver
) : EventStore {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override suspend fun save(event: DomainEvent) {
        val storedEvent = StoredEvent(
            aggregateId = event.aggregateId,
            eventId = event.eventId,
            eventType = event.type,
            occurredOn = event.occurredOn,
            eventBody = eventSerializer.serialize(event)
        )
        storedEventRepository.save(storedEvent)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override suspend fun saveAll(events: List<DomainEvent>) {
        val storedEvents = events.map { event ->
            StoredEvent(
                aggregateId = event.aggregateId,
                eventId = event.eventId,
                eventType = event.type,
                occurredOn = event.occurredOn,
                eventBody = eventSerializer.serialize(event)
            )
        }
        storedEventRepository.saveAll(storedEvents)
    }

    @Transactional(readOnly = true)
    override suspend fun findByAggregateId(aggregateId: UUID, sort: Sort): List<DomainEvent> {
        val storedEvents = storedEventRepository.findByAggregateId(aggregateId, sort)
        return storedEvents.mapNotNull { storedEvent ->
            deserializeEvent(storedEvent)
        }
    }

    @Transactional(readOnly = true)
    override suspend fun findByType(type: String): List<DomainEvent> {
        val storedEvents = storedEventRepository.findByEventType(type, Sort.by(Sort.Direction.ASC, "occurredOn"))
        return storedEvents.mapNotNull { storedEvent ->
            deserializeEvent(storedEvent)
        }
    }

    private fun deserializeEvent(storedEvent: StoredEvent): DomainEvent? {
        val eventClass = eventClassResolver.resolveClass(storedEvent.eventType)
        return eventClass?.let { clazz ->
            try {
                @Suppress("UNCHECKED_CAST")
                eventSerializer.deserialize(storedEvent.eventBody, clazz as Class<DomainEvent>)
            } catch (e: Exception) {
                null
            }
        }
    }
}
