package com.projecthub.base.shared.domain.event;

import java.time.Instant;
import java.util.Map;

/**
 * Represents an activity event in the system.
 * 
 * @param userId The ID of the user who performed the activity
 * @param activityType The type of activity performed
 * @param entityType The type of entity involved (e.g., "project", "task")
 * @param entityId The ID of the entity involved
 * @param metadata Additional contextual data about the activity
 * @param timestamp When the activity occurred
 */
public record ActivityEvent(
    String userId,
    ActivityType activityType,
    String entityType,
    String entityId,
    Map<String, Object> metadata,
    Instant timestamp
) {}