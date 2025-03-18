package com.projecthub.events.sourcing

import com.projecthub.domain.BaseAggregateRoot
import com.projecthub.events.EventStore
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import java.util.*

/**
 * Handler for supporting event sourcing operations.
 * This class enables reconstructing aggregates from their event history.
 */
@Component
class EventSourcingHandler<T : BaseAggregateRoot>(
    private val eventStore: EventStore
) {
    /**
     * Load an aggregate by applying all its historical events.
     * @param aggregateId The ID of the aggregate to load
     * @param aggregateFactory A factory function to create a new instance of the aggregate
     * @return The reconstructed aggregate
     */
    suspend fun load(aggregateId: UUID, aggregateFactory: () -> T): T {
        val events = eventStore.findByAggregateId(
            aggregateId,
            Sort.by(Sort.Direction.ASC, "occurredOn")
        )

        if (events.isEmpty()) {
            throw AggregateNotFoundException("Aggregate with ID $aggregateId not found")
        }

        val aggregate = aggregateFactory()

        if (aggregate is EventSourcingAggregate<*>) {
            aggregate.loadFromHistory(events)
        } else {
            throw IllegalArgumentException("Aggregate must implement EventSourcingAggregate interface")
        }

        return aggregate
    }
}

/**
 * Exception thrown when an aggregate cannot be found.
 */
class AggregateNotFoundException(message: String) : RuntimeException(message)
