package com.projecthub.base.shared.domain.event;

import java.time.Instant;
import java.util.Map;

/**
 * Represents a notification event in the system.
 * 
 * @param recipientId The ID of the user who should receive the notification
 * @param senderId The ID of the user who triggered the notification (optional)
 * @param type The type of notification
 * @param title Short title/summary of the notification
 * @param message Detailed notification message
 * @param metadata Additional contextual data about the notification
 * @param timestamp When the notification was created
 * @param read Whether the notification has been read
 */
public record NotificationEvent(
    String recipientId,
    String senderId,
    NotificationType type,
    String title,
    String message,
    Map<String, Object> metadata,
    Instant timestamp,
    boolean read
) {}