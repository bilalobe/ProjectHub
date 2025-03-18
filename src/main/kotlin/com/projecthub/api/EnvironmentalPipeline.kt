package com.projecthub.api

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import com.rabbitmq.client.Channel

data class EnvironmentalData(val sensorId: String, val value: Double, val timestamp: Long)

fun Application.environmentalPipeline(channel: Channel) {
    routing {
        post("/sensor-data") {
            val data = call.receive<EnvironmentalData>()
            val message = "${data.sensorId},${data.value},${data.timestamp}"
            channel.basicPublish(
                "environmental-exchange",
                "env.data.raw",
                null,
                message.toByteArray()
            )
            call.respondText("Sensor data published to RabbitMQ")
        }
    }
}
