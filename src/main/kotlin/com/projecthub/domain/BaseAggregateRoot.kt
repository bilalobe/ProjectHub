package com.projecthub.domain

import com.projecthub.events.DomainEvent

abstract class BaseAggregateRoot : BaseEntity(), AggregateRoot<BaseEntity> {
    private val domainEvents = mutableListOf<DomainEvent>()

    protected fun registerEvent(event: DomainEvent) {
        domainEvents.add(event)
    }

    fun clearEvents() {
        domainEvents.clear()
    }

    fun getDomainEvents(): List<DomainEvent> = domainEvents.toList()
}
