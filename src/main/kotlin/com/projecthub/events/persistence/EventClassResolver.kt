package com.projecthub.events.persistence

import com.projecthub.events.DomainEvent
import com.projecthub.domain.project.events.ProjectEvent
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AssignableTypeFilter
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

/**
 * Service for resolving event classes from their type names.
 */
@Component
class EventClassResolver {
    private val logger = LoggerFactory.getLogger(EventClassResolver::class.java)
    private val eventClassMap = mutableMapOf<String, Class<*>>()

    @PostConstruct
    fun initialize() {
        val scanner = ClassPathScanningCandidateComponentProvider(false)
        scanner.addIncludeFilter(AssignableTypeFilter(DomainEvent::class.java))

        val basePackage = "com.projecthub"
        val candidates = scanner.findCandidateComponents(basePackage)

        for (candidate in candidates) {
            try {
                val clazz = Class.forName(candidate.beanClassName)
                val simpleName = clazz.simpleName
                eventClassMap[simpleName] = clazz
                logger.debug("Registered event class: $simpleName -> $clazz")
            } catch (e: Exception) {
                logger.error("Failed to register event class: ${candidate.beanClassName}", e)
            }
        }

        // Register sealed class subclasses manually since they won't be picked up by the scanner
        registerSealedClassEvents()

        logger.info("Registered ${eventClassMap.size} event classes")
    }

    /**
     * Register all subclasses of sealed event classes
     */
    private fun registerSealedClassEvents() {
        // ProjectEvent sealed class subclasses
        registerEventType("ProjectCreatedEvent", ProjectEvent.Created::class.java)
        registerEventType("ProjectUpdatedEvent", ProjectEvent.Updated::class.java)
        registerEventType("ProjectCompletedEvent", ProjectEvent.Completed::class.java)
        registerEventType("ProjectTeamMemberAddedEvent", ProjectEvent.TeamMemberAdded::class.java)
        registerEventType("ProjectTeamMemberRemovedEvent", ProjectEvent.TeamMemberRemoved::class.java)
    }

    private fun registerEventType(eventType: String, eventClass: Class<*>) {
        eventClassMap[eventType] = eventClass
        logger.debug("Registered sealed class event: $eventType -> $eventClass")
    }

    /**
     * Resolve a class from its type name.
     */
    fun resolveClass(eventType: String): Class<*>? {
        return eventClassMap[eventType]
    }
}
