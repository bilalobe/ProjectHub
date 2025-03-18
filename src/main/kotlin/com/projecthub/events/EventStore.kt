package com.projecthub.events

import org.springframework.data.domain.Sort
import java.util.*

interface EventStore {
    suspend fun save(event: DomainEvent)
    suspend fun saveAll(events: List<DomainEvent>)
    suspend fun findByAggregateId(aggregateId: UUID, sort: Sort = Sort.by("occurredOn")): List<DomainEvent>
    suspend fun findByType(type: String): List<DomainEvent>
}
