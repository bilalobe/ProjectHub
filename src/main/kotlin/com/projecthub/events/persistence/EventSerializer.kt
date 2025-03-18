package com.projecthub.events.persistence

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.projecthub.events.DomainEvent
import org.springframework.stereotype.Component

/**
 * Service for serializing and deserializing domain events.
 * This enables storing events in the database and reconstructing them later.
 */
@Component
class EventSerializer(private val objectMapper: ObjectMapper) {

    /**
     * Serialize a domain event to JSON.
     */
    fun serialize(event: DomainEvent): String {
        return objectMapper.writeValueAsString(event)
    }

    /**
     * Deserialize JSON to a domain event.
     */
    inline fun <reified T : DomainEvent> deserialize(eventBody: String): T {
        return objectMapper.readValue(eventBody)
    }

    /**
     * Deserialize JSON to a domain event using a class reference.
     */
    fun <T : DomainEvent> deserialize(eventBody: String, eventClass: Class<T>): T {
        return objectMapper.readValue(eventBody, eventClass)
    }
}
