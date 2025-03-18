package com.projecthub.api

import com.projecthub.resilience.CircuitBreakerState
import com.projecthub.resilience.ResilienceService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

/**
 * API controller for monitoring resilience patterns
 */
fun Application.configureResilienceMonitoring() {
    val resilienceService by inject<ResilienceService>()
    
    routing {
        route("/api/resilience") {
            get {
                val states = resilienceService.getAllCircuitBreakerStates()
                val response = states.map { (name, state) ->
                    CircuitBreakerStatusDto(
                        name = name,
                        state = state.name,
                        isOpen = state == CircuitBreakerState.OPEN,
                        isClosed = state == CircuitBreakerState.CLOSED,
                        isHalfOpen = state == CircuitBreakerState.HALF_OPEN
                    )
                }
                call.respond(response)
            }
            
            get("/{name}") {
                val name = call.parameters["name"] ?: return@get call.respondText(
                    "Missing or malformed circuit breaker name",
                    status = HttpStatusCode.BadRequest
                )
                
                val state = resilienceService.getCircuitBreakerState(name)
                if (state == null) {
                    call.respond(HttpStatusCode.NotFound, "No circuit breaker found with name: $name")
                } else {
                    call.respond(
                        CircuitBreakerStatusDto(
                            name = name,
                            state = state.name,
                            isOpen = state == CircuitBreakerState.OPEN,
                            isClosed = state == CircuitBreakerState.CLOSED,
                            isHalfOpen = state == CircuitBreakerState.HALF_OPEN
                        )
                    )
                }
            }
            
            post("/{name}/reset") {
                val name = call.parameters["name"] ?: return@post call.respondText(
                    "Missing or malformed circuit breaker name",
                    status = HttpStatusCode.BadRequest
                )
                
                val state = resilienceService.getCircuitBreakerState(name)
                if (state == null) {
                    call.respond(HttpStatusCode.NotFound, "No circuit breaker found with name: $name")
                } else {
                    resilienceService.resetAllCircuitBreakers()
                    call.respond(HttpStatusCode.OK, "Circuit breaker reset: $name")
                }
            }
            
            post("/reset-all") {
                resilienceService.resetAllCircuitBreakers()
                call.respond(HttpStatusCode.OK, "All circuit breakers reset")
            }
        }
    }
}

@Serializable
data class CircuitBreakerStatusDto(
    val name: String,
    val state: String,
    val isOpen: Boolean,
    val isClosed: Boolean,
    val isHalfOpen: Boolean
)
