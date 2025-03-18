package foundation.adapter.`in`.messaging

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class EventConsumer {

    @KafkaListener(topics = ["project-created-topic"], groupId = "project-group")
    fun consumeProjectCreatedEvent(message: String) {
        println("Received project created event: $message")
        // Process the event
    }
}
