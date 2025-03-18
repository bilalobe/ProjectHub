package com.projecthub.events

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class EventBus(private val handlers: List<EventHandler<*>>) : EventPublisher {
    private val logger = LoggerFactory.getLogger(EventBus::class.java)
    private val eventHandlerMap = mutableMapOf<KClass<out DomainEvent>, MutableList<EventHandler<DomainEvent>>>()

    init {
        @Suppress("UNCHECKED_CAST")
        handlers.forEach { handler ->
            val eventClass = handler.eventType.kotlin
            eventHandlerMap
                .getOrPut(eventClass as KClass<out DomainEvent>) { mutableListOf() }
                .add(handler as EventHandler<DomainEvent>)
        }
        logger.info("EventBus initialized with ${handlers.size} handlers for ${eventHandlerMap.size} event types")
    }

    override suspend fun publish(event: DomainEvent) {
        val handlers = eventHandlerMap[event::class] ?: return
        handlers.forEach { handler ->
            try {
                handler.handle(event)
            } catch (e: Exception) {
                logger.error("Error handling event ${event.type}", e)
            }
        }
    }

    override suspend fun publishAll(events: List<DomainEvent>) {
        events.forEach { publish(it) }
    }
}
