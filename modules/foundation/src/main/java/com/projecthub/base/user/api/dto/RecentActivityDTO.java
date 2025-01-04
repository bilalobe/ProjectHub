package com.projecthub.base.user.api.dto;

/**
 * DTO for recent activity data.
 */
public record RecentActivityDTO(
    String timestamp,
    String activity,
    String user
) {
}
