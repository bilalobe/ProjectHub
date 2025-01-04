package com.projecthub.base.user.api.dto;

import java.util.Set;
import java.util.UUID;

/**
 * Data Transfer Object for the AppUser entity.
 */
public record AppUserDTO(
    UUID id,
    String username,
    String email,
    String firstName,
    String lastName,
    boolean enabled,
    boolean accountNonLocked,
    boolean verified,
    String avatarUrl,
    String statusMessage,
    int postCount,
    int followerCount,
    int followingCount,
    Set<String> roles
) {
}
