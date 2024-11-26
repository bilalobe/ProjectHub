package com.projecthub.dto;

import jakarta.validation.constraints.Email;
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

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private final String email;

    @NotBlank(message = "First name is mandatory")
    private final String firstName;

    @NotBlank(message = "Last name is mandatory")
    private final String lastName;

    private final Long teamId;

    /**
     * Constructs an AppUserSummary with specified values.
     *
     * @param id        the user ID
     * @param username  the username
     * @param email     the email
     * @param firstName the first name
     * @param lastName  the last name
     * @param teamId    the team ID
     */
    public AppUserSummary(Long id, String username, String email, String firstName, String lastName, Long teamId) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
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
     * Gets the email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
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