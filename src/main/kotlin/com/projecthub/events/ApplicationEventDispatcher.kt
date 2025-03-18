package com.projecthub.events

import com.projecthub.domain.BaseAggregateRoot
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager

@Component
class ApplicationEventDispatcher(
    private val eventPublisher: EventPublisher,
    private val eventStore: EventStore
) {
    @Transactional(propagation = Propagation.MANDATORY)
    fun dispatchEventsFrom(aggregate: BaseAggregateRoot) {
        val events = aggregate.getDomainEvents()
        if (events.isEmpty()) return

        // Store events in EventStore
        runBlocking {
            eventStore.saveAll(events)
        }

        // Register transaction callback to publish events when transaction completes
        TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
            override fun afterCommit() {
                runBlocking {
                    eventPublisher.publishAll(events)
                }
            }
        })

        // Clear events from aggregate after they've been stored
        aggregate.clearEvents()
    }
}
