package com.projecthub.base.shared.domain.event;

/**
 * Represents different types of notifications in the system.
 */
public enum NotificationType {
    // Direct interactions
    MENTION,
    DIRECT_MESSAGE,
    
    // Task notifications
    TASK_ASSIGNED,
    TASK_DUE_SOON,
    TASK_OVERDUE,
    TASK_COMPLETED,
    
    // Project notifications
    PROJECT_INVITATION,
    PROJECT_UPDATE,
    PROJECT_DEADLINE,
    
    // Team notifications
    TEAM_INVITATION,
    TEAM_UPDATE,
    
    // System notifications
    SYSTEM_ANNOUNCEMENT,
    MAINTENANCE_ALERT,
    SECURITY_ALERT,
    
    // Social notifications
    NEW_FOLLOWER,
    ACHIEVEMENT_UNLOCKED,
    
    // Review notifications
    REVIEW_REQUESTED,
    REVIEW_SUBMITTED,
    REVIEW_APPROVED,
    REVIEW_REJECTED
}