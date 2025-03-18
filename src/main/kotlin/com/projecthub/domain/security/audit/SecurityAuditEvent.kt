package com.projecthub.domain.security.audit

import com.projecthub.domain.BaseEntity
import jakarta.persistence.*
import java.time.Instant

/**
 * Entity representing a security-related event that should be audited.
 * This records information about authentication, authorization, and other security events.
 */
@Entity
@Table(name = "security_audit_events")
class SecurityAuditEvent(
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val type: SecurityEventType,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val outcome: SecurityEventOutcome,

    @Column(name = "user_id")
    val userId: String? = null,

    @Column
    val username: String? = null,

    @Column(name = "target_user_id")
    val targetUserId: String? = null,

    @Column(name = "resource_id")
    val resourceId: String? = null,

    @Column(name = "ip_address")
    val ipAddress: String? = null,

    @Column(nullable = false)
    val timestamp: Instant = Instant.now(),

    @Column(length = 1000)
    val details: String? = null
) : BaseEntity()