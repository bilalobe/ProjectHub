package com.projecthub.base.security.audit;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Represents a security audit event in the system.
 * Used for tracking security-related actions for compliance and monitoring.
 */
@Data
@Builder
public class SecurityAuditEvent {
    
    /**
     * Unique identifier for the event.
     */
    private String eventId;
    
    /**
     * The type of security event.
     */
    private SecurityEventType eventType;
    
    /**
     * When the event occurred.
     */
    private Instant timestamp;
    
    /**
     * The user who triggered the event.
     */
    private String userId;
    
    /**
     * The IP address from which the event originated.
     */
    private String sourceIp;
    
    /**
     * The type of resource affected (e.g., "project", "user", "data").
     */
    private String resourceType;
    
    /**
     * The identifier of the affected resource.
     */
    private String resourceId;
    
    /**
     * Additional details about the event, typically in JSON format.
     */
    private String details;
    
    /**
     * Whether the action was successful.
     */
    private boolean successful;
}