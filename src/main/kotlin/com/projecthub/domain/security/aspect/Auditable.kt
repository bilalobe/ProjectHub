package com.projecthub.domain.security.aspect

import com.projecthub.domain.security.audit.SecurityEventType

/**
 * Annotation to mark methods that should be audited.
 * When a method is annotated with @Auditable, the AuditAspect will
 * intercept calls to that method and log them as security audit events.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Auditable(
    /**
     * The type of security event being audited.
     */
    val type: SecurityEventType = SecurityEventType.SYSTEM_CONFIGURATION,

    /**
     * Optional message to include in the audit log.
     */
    val message: String = "",

    /**
     * Whether to include the method parameters in the audit log.
     * Note: Be careful with sensitive data in parameters.
     */
    val includeParams: Boolean = false
)
