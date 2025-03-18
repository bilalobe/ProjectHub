package com.projecthub.infrastructure.messaging

import com.projecthub.core.application.common.port.out.DomainEventPublisher
import com.projecthub.core.domain.event.DomainEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class SpringDomainEventPublisher(private val applicationEventPublisher: ApplicationEventPublisher) :
    DomainEventPublisher {

    override fun publish(event: DomainEvent) {
        applicationEventPublisher.publishEvent(event)
    }
}
