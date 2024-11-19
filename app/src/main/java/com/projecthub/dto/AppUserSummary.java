package com.projecthub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for User entity.
 * Used for creating and updating users.
 */
public class AppUserSummary {

    private Long id;

    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    private Long teamId;

    public AppUserSummary() {
    }

    public AppUserSummary(Long id, String username, String password, Long teamId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.teamId = teamId;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     * Note: Ensure that passwords are encoded before setting.
     *
     * @param password Raw password string
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }
}
