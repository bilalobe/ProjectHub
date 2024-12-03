package com.projecthub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Used for transferring user data between processes.
 */
public class AppUserSummary {
    private final UUID id;

    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private final String username;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private final String email;

    @NotBlank(message = "First name is mandatory")
    private final String firstName;

    @NotBlank(message = "Last name is mandatory")
    private final String lastName;

    private final Long teamId;

    public AppUserSummary(UUID id, String username, String email, String firstName, String lastName, Long teamId) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.teamId = teamId;
    }

    // Getters only, no setters to ensure immutability
    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getTeamId() {
        return teamId;
    }
}