package com.projecthub.core.dto;

import java.util.UUID;

/**
 * Data Transfer Object for user profile-related data.
 */
public record AppUserProfileDTO(
    UUID id,
    String username,
    String email,
    String firstName,
    String lastName,
    String avatarUrl,
    String statusMessage,
    int postCount,
    int followerCount,
    int followingCount
) {
}