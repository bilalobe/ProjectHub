package com.projecthub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Data Transfer Object for user profile-related data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUserProfileDTO {

    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private String statusMessage;
    private int postCount;
    private int followerCount;
    private int followingCount;
}