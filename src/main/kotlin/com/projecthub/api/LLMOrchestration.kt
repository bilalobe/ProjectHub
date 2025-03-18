package com.projecthub.api

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import com.rabbitmq.client.Channel

fun Application.llmOrchestration(channel: Channel) {
    routing {
        post("/llm/process") {
            val requestData = call.receive<String>()
            channel.basicPublish(
                "llm-exchange",
                "llm.process.request",
                null,
                requestData.toByteArray()
            )
            call.respondText("Request sent to LLM processing queue")
        }
    }
}
