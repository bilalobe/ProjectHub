package com.projecthub.messaging

import com.rabbitmq.client.ConnectionFactory
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow

class RabbitMqMessagingService(
    private val host: String = "localhost",
    private val queueName: String = "projecthub.events"
) {
    private val connectionFactory = ConnectionFactory().apply { this.host = host }
    private val connection = connectionFactory.newConnection()
    private val channel = connection.createChannel()

    init {
        channel.queueDeclare(queueName, true, false, false, null)
    }

    fun publishEvent(event: String) {
        channel.basicPublish("", queueName, null, event.toByteArray())
    }

    fun consumeEvents(): Flow<String> {
        val messageChannel = Channel<String>()
        val consumer = { _: String, delivery: com.rabbitmq.client.Delivery ->
            messageChannel.trySend(String(delivery.body))
        }
        channel.basicConsume(queueName, true, consumer)
        return messageChannel.consumeAsFlow()
    }

    fun close() {
        channel.close()
        connection.close()
    }
}
