package com.projecthub.api

import com.rabbitmq.client.Channel

data class SecurityEvent(val type: String, val details: String)

fun publishSecurityEvent(channel: Channel, event: SecurityEvent) {
    val message = "${event.type},${event.details}"
    channel.basicPublish(
        "security-exchange",
        "security.event.${event.type}",
        null,
        message.toByteArray()
    )
}
