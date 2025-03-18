package com.projecthub.domain.security.audit

import java.time.Instant

/**
 * Repository interface for storing and retrieving security audit events.
 */
interface SecurityAuditRepository {

    /**
     * Saves a security audit event to the repository.
     *
     * @param event The security audit event to save
     * @return The saved security audit event
     */
    suspend fun save(event: SecurityAuditEvent): SecurityAuditEvent

    /**
     * Finds security audit events by user ID.
     *
     * @param userId The ID of the user to find events for
     * @return A list of security audit events for the given user
     */
    suspend fun findByUserId(userId: String): List<SecurityAuditEvent>

    /**
     * Finds security audit events by type.
     *
     * @param type The type of events to find
     * @return A list of security audit events of the given type
     */
    suspend fun findByType(type: SecurityEventType): List<SecurityAuditEvent>

    /**
     * Finds security audit events that occurred within a time range.
     *
     * @param start The start of the time range
     * @param end The end of the time range
     * @return A list of security audit events within the given time range
     */
    suspend fun findByTimestampBetween(start: Instant, end: Instant): List<SecurityAuditEvent>

    /**
     * Finds failed authentication attempts for a given username.
     *
     * @param username The username to find failed authentication attempts for
     * @param since The time after which to find failed attempts
     * @return A list of failed authentication attempts
     */
    suspend fun findFailedAuthenticationAttempts(username: String, since: Instant): List<SecurityAuditEvent>

    /**
     * Counts the number of security audit events by type and outcome.
     *
     * @param type The type of events to count
     * @param outcome The outcome of events to count
     * @param since The time after which to count events
     * @return The count of events matching the criteria
     */
    suspend fun countByTypeAndOutcomeSince(
        type: SecurityEventType,
        outcome: SecurityEventOutcome,
        since: Instant
    ): Int
}
