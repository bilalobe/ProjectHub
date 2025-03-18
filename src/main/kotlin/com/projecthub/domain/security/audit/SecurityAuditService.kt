package com.projecthub.domain.security.audit

import com.projecthub.domain.security.permission.Permission
import com.projecthub.domain.user.UserId
import org.springframework.stereotype.Service
import java.time.Instant
import org.slf4j.LoggerFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Service for logging security-related events for audit purposes.
 * This provides a centralized way to track security events throughout the application.
 */
@Service
class SecurityAuditService(
    private val securityAuditRepository: SecurityAuditRepository
) {
    private val logger = LoggerFactory.getLogger(SecurityAuditService::class.java)

    /**
     * Records a security event such as login, permission check, or access attempt.
     */
    suspend fun recordEvent(event: SecurityAuditEvent) = withContext(Dispatchers.IO) {
        try {
            logger.debug("Recording security event: {}", event)
            securityAuditRepository.save(event)
        } catch (e: Exception) {
            logger.error("Failed to record security event: {}", event, e)
            // Even if DB save fails, we should still log the event
        }
    }

    /**
     * Records a successful authentication event.
     */
    suspend fun recordSuccessfulAuthentication(userId: UserId, ipAddress: String) {
        recordEvent(
            SecurityAuditEvent(
                type = SecurityEventType.AUTHENTICATION,
                outcome = SecurityEventOutcome.SUCCESS,
                userId = userId.value,
                ipAddress = ipAddress,
                timestamp = Instant.now(),
                details = "User successfully authenticated"
            )
        )
    }

    /**
     * Records a failed authentication event.
     */
    suspend fun recordFailedAuthentication(username: String, ipAddress: String, reason: String) {
        recordEvent(
            SecurityAuditEvent(
                type = SecurityEventType.AUTHENTICATION,
                outcome = SecurityEventOutcome.FAILURE,
                username = username,
                ipAddress = ipAddress,
                timestamp = Instant.now(),
                details = "Authentication failed: $reason"
            )
        )
    }

    /**
     * Records an authorization check event.
     */
    suspend fun recordAuthorizationCheck(
        userId: UserId,
        permission: Permission,
        resourceId: String? = null,
        outcome: SecurityEventOutcome
    ) {
        val details = if (resourceId != null) {
            "Permission check: ${permission.value} for resource $resourceId"
        } else {
            "Permission check: ${permission.value}"
        }

        recordEvent(
            SecurityAuditEvent(
                type = SecurityEventType.AUTHORIZATION,
                outcome = outcome,
                userId = userId.value,
                timestamp = Instant.now(),
                details = details
            )
        )
    }

    /**
     * Records a role change event.
     */
    suspend fun recordRoleChange(
        adminId: UserId,
        targetUserId: UserId,
        roleId: String,
        isAssign: Boolean
    ) {
        val action = if (isAssign) "assigned to" else "removed from"
        recordEvent(
            SecurityAuditEvent(
                type = SecurityEventType.ROLE_CHANGE,
                outcome = SecurityEventOutcome.SUCCESS,
                userId = adminId.value,
                timestamp = Instant.now(),
                details = "Role $roleId $action user ${targetUserId.value}",
                targetUserId = targetUserId.value
            )
        )
    }
}

/**
 * Enum representing the type of security event
 */
enum class SecurityEventType {
    AUTHENTICATION,
    AUTHORIZATION,
    ROLE_CHANGE,
    PERMISSION_CHANGE,
    SYSTEM_CONFIGURATION,
    DATA_ACCESS
}

/**
 * Enum representing the outcome of a security event
 */
enum class SecurityEventOutcome {
    SUCCESS,
    FAILURE,
    DENIED,
    WARNING
}