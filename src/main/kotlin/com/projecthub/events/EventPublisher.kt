package com.projecthub.events

interface EventPublisher {
    suspend fun publish(event: DomainEvent)
    suspend fun publishAll(events: List<DomainEvent>)
}
