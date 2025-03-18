package com.projecthub.events

interface EventHandler<T : DomainEvent> {
    val eventType: Class<T>
    suspend fun handle(event: T)
}
