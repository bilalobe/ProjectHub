package com.projecthub.base.shared.domain.event;

/**
 * Represents different types of activities that can occur in the system.
 */
public enum ActivityType {
    // Project-related activities
    PROJECT_CREATED,
    PROJECT_UPDATED,
    PROJECT_DELETED,
    PROJECT_SHARED,
    
    // Task-related activities
    TASK_CREATED,
    TASK_UPDATED,
    TASK_COMPLETED,
    TASK_ASSIGNED,
    
    // Comment-related activities
    COMMENT_ADDED,
    COMMENT_UPDATED,
    COMMENT_DELETED,
    
    // Team-related activities
    TEAM_CREATED,
    TEAM_UPDATED,
    TEAM_MEMBER_ADDED,
    TEAM_MEMBER_REMOVED,
    
    // User-related activities
    USER_JOINED,
    USER_UPDATED_PROFILE,
    USER_ROLE_CHANGED,
    
    // Collaboration activities
    MENTION,
    DOCUMENT_SHARED,
    DOCUMENT_EDITED
}