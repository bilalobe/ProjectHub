package com.projecthub.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

/**
 * Data Transfer Object for the AppUser entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUserDTO {

    private UUID id;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private boolean enabled;
    private boolean accountNonLocked;
    private boolean verified;
    private String avatarUrl;
    private String statusMessage;
    private int postCount;
    private int followerCount;
    private int followingCount;
    private Set<String> roles;
}