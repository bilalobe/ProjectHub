package com.projecthub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for the AppUser entity.
 * Used for transferring user data between processes.
 */
public class AppUserSummary {
    private final Long id;

    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private final String username;

    private final Long teamId;

    /**
     * Constructs an AppUserSummary with specified values.
     *
     * @param id      the user ID
     * @param username the username
     * @param teamId   the team ID
     */
    public AppUserSummary(Long id, String username, String password, Long teamId) {
        this.id = id;
        this.username = username;
        this.teamId = teamId;
    }

    /**
     * Gets the user ID.
     *
     * @return the user ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the team ID.
     *
     * @return the team ID
     */
    public Long getTeam() {
        return teamId;
    }
}