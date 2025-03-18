package com.projecthub.infrastructure.ktor.plugins

import io.github.damir.denis.tudor.ktor.server.rabbitmq.RabbitMQ
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.*
import io.github.damir.denis.tudor.ktor.server.rabbitmq.rabbitMQ
import io.ktor.server.application.*
import io.ktor.server.websocket.*
import io.ktor.server.sse.*
import kotlinx.coroutines.Dispatchers

fun Application.configureMessaging() {
    install(WebSockets) {
        // Configure WebSocket settings
    }

    install(SSE) {
        // Configure Server-Sent Events
    }

    install(RabbitMQ) {
        uri = "amqp://guest:guest@localhost:5672"
        defaultConnectionName = "default-connection"
        dispatcherThreadPollSize = 4
        tlsEnabled = false
    }

    configureRabbitMQBindings()
    configureRabbitMQConsumers()
}

private fun Application.configureRabbitMQBindings() {
    rabbitmq {
        queueBind {
            queue = "dlq"
            exchange = "dlx"
            routingKey = "dlq-dlx"
            exchangeDeclare {
                exchange = "dlx"
                type = "direct"
            }
            queueDeclare {
                queue = "dlq"
                durable = true
            }
        }
    }

    rabbitmq {
        queueBind {
            queue = "test-queue"
            exchange = "test-exchange"
            routingKey = "test-routing-key"
            exchangeDeclare {
                exchange = "test-exchange"
                type = "direct"
            }
            queueDeclare {
                queue = "test-queue"
                arguments = mapOf(
                    "x-dead-letter-exchange" to "dlx",
                    "x-dead-letter-routing-key" to "dlq-dlx"
                )
            }
        }
    }
}

private fun Application.configureRabbitMQConsumers() {
    rabbitmq {
        basicConsume {
            autoAck = true
            queue = "test-queue"
            dispatcher = Dispatchers.rabbitMQ
            coroutinePollSize = 100
            deliverCallback<String> { tag, message ->
                log.info("Received message: $message")
            }
        }
    }
}
