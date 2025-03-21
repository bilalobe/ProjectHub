package com.projecthub.security

import com.projecthub.session.UserSession
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.server.sse.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.websocket.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.channels.ClosedSendChannelException
import org.slf4j.LoggerFactory

/**
 * Ktor-specific adapter for applying the unified security policy to
 * all types of routes: REST, WebSocket, and SSE.
 */
class KtorSecurityAdapter(
    private val securityManager: UnifiedSecurityManager
) {
    private val logger = LoggerFactory.getLogger(KtorSecurityAdapter::class.java)
    
    /**
     * Configure the Ktor application with unified security
     */
    fun configure(application: Application) {
        // Configure authentication
        application.authentication {
            // Add JWT authentication
            jwt("jwt-auth") {
                // JWT validation configuration here...
                validate { jwtCredential ->
                    // Convert JWT claims to UserSession
                    val userId = jwtCredential.payload.getClaim("userId").asString()
                    val sessionId = jwtCredential.payload.getClaim("sessionId").asString()
                    
                    UserSession(userId, sessionId)
                }
            }
            
            // Add Basic authentication for testing/development
            basic("basic-auth") {
                validate { credentials ->
                    // Simple validation for development
                    if (credentials.name == "admin" && credentials.password == "admin") {
                        UserSession("admin", "admin-session")
                    } else {
                        null
                    }
                }
            }
        }
        
        // Install interceptor for all routes
        application.intercept(ApplicationCallPipeline.Call) {
            // This ensures security checks are applied to all routes,
            // regardless of whether they're API, WebSocket, or SSE
            try {
                val session = call.principal<UserSession>()
                // Store session in call attributes for later use
                if (session != null) {
                    call.attributes.put(SessionAttributeKey, session)
                }
            } catch (e: Exception) {
                logger.error("Error during security interception", e)
            }
        }
    }
    
    /**
     * Extension function for Routes to protect them with a permission check
     */
    fun Route.requirePermission(objectName: String, operation: String, objectId: String? = null) {
        intercept(ApplicationCallPipeline.Call) {
            val session = call.attributes.getOrNull(SessionAttributeKey)
            if (session == null) {
                call.respond(HttpStatusCode.Unauthorized, "Authentication required")
                finish()
                return@intercept
            }
            
            if (!securityManager.checkPermission(session, objectName, operation, objectId)) {
                call.respond(HttpStatusCode.Forbidden, "Permission denied: $objectName:$operation")
                finish()
                return@intercept
            }
        }
    }
    
    /**
     * Extension function to protect WebSocket routes
     */
    fun Route.webSocketWithPermission(
        path: String,
        objectName: String,
        operation: String,
        objectId: String? = null,
        handler: suspend WebSocketServerSession.() -> Unit
    ) {
        webSocket(path) {
            val session = call.attributes.getOrNull(SessionAttributeKey)
            if (session == null) {
                close(CloseReason(CloseReason.Codes.POLICY_VIOLATION, "Authentication required"))
                return@webSocket
            }
            
            if (!securityManager.checkPermission(session, objectName, operation, objectId)) {
                close(CloseReason(CloseReason.Codes.POLICY_VIOLATION, "Permission denied: $objectName:$operation"))
                return@webSocket
            }
            
            try {
                handler()
            } catch (e: ClosedSendChannelException) {
                logger.info("WebSocket client disconnected")
            } catch (e: Exception) {
                logger.error("Error in secured WebSocket", e)
            }
        }
    }
    
    /**
     * Extension function to protect SSE routes
     */
    fun Route.sseWithPermission(
        path: String,
        objectName: String,
        operation: String,
        objectId: String? = null,
        handler: suspend SSEServerSendChannel.() -> Unit
    ) {
        sse(path) {
            val session = call.attributes.getOrNull(SessionAttributeKey)
            if (session == null) {
                // Cannot respond with status codes in SSE, so just close
                close()
                return@sse
            }
            
            if (!securityManager.checkPermission(session, objectName, operation, objectId)) {
                // Cannot respond with status codes in SSE, so just close
                close()
                return@sse
            }
            
            try {
                handler()
            } catch (e: Exception) {
                logger.error("Error in secured SSE", e)
            }
        }
    }
    
    companion object {
        private val SessionAttributeKey = AttributeKey<UserSession>("UserSession")
    }
}