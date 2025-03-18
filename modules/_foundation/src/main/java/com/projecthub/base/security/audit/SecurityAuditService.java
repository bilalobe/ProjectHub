package com.projecthub.base.security.audit;

import java.time.Instant;
import java.util.List;

/**
 * Service for recording and querying security-related audit events.
 * Part of the application-specific security infrastructure.
 */
public interface SecurityAuditService {
    
    /**
     * Records a security event in the audit log.
     *
     * @param eventType The type of security event
     * @param userId The ID of the user who triggered the event
     * @param resourceType The type of resource affected
     * @param resourceId The ID of the resource affected
     * @param details Additional event details as JSON string
     */
    void logSecurityEvent(
            SecurityEventType eventType,
            String userId,
            String resourceType, 
            String resourceId,
            String details);
    
    /**
     * Retrieves recent security events for a specific user.
     *
     * @param userId The ID of the user
     * @param limit Maximum number of events to retrieve
     * @return List of security audit events
     */
    List<SecurityAuditEvent> getEventsForUser(String userId, int limit);
    
    /**
     * Retrieves security events by criteria.
     *
     * @param eventType Filter by event type (optional)
     * @param startTime Filter by start time (optional)
     * @param endTime Filter by end time (optional)
     * @param resourceType Filter by resource type (optional)
     * @param limit Maximum number of events to retrieve
     * @return List of matching security audit events
     */
    List<SecurityAuditEvent> getEvents(
            SecurityEventType eventType, 
            Instant startTime, 
            Instant endTime,
            String resourceType,
            int limit);
            
    /**
     * Records failed authentication attempts.
     *
     * @param username The username used in the attempt
     * @param ipAddress The IP address the attempt came from
     * @param reason The reason for the failure
     */
    void logAuthenticationFailure(String username, String ipAddress, String reason);
    
    /**
     * Records successful authentication.
     *
     * @param userId The ID of the authenticated user
     * @param ipAddress The IP address used for authentication
     */
    void logSuccessfulAuthentication(String userId, String ipAddress);
}