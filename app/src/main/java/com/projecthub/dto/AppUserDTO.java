package com.projecthub.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.opencsv.bean.CsvBindByName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Data Transfer Object for the AppUser entity.
 * Used for transferring user data between processes.
 */
public class AppUserDTO {

    /**
     * The unique identifier of the user.
     */
    @CsvBindByName(column = "id")
    private UUID id;

    /**
     * The username of the user.
     */
    @CsvBindByName(column = "username")
    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    /**
     * The password of the user.
     * This field is only used for deserialization (input) and not for serialization (output).
     */
    @JsonProperty(access = Access.WRITE_ONLY)
    @NotBlank(message = "Password is mandatory")
    private String password;

    /**
     * The email address of the user.
     */
    @CsvBindByName(column = "email")
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;

    /**
     * The first name of the user.
     */
    @CsvBindByName(column = "firstName")
    @NotBlank(message = "First name is mandatory")
    private String firstName;

    /**
     * The last name of the user.
     */
    @CsvBindByName(column = "lastName")
    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    // No-argument constructor
    public AppUserDTO() {
        // Default constructor
    }

    /**
     * Constructs a new AppUserDTO with the specified details.
     *
     * @param id        the unique identifier of the user
     * @param username  the username of the user
     * @param email     the email address of the user
     * @param firstName the first name of the user
     * @param lastName  the last name of the user
     */
    public AppUserDTO(UUID id, String username, String email, String firstName, String lastName) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Gets the unique identifier of the user.
     *
     * @return the user's unique identifier
     */
    public UUID getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the user.
     *
     * @param id the user's unique identifier
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Gets the username of the user.
     *
     * @return the user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     *
     * @param username the user's username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password of the user.
     *
     * @return the user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password the user's password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the email address of the user.
     *
     * @return the user's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email the user's email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the first name of the user.
     *
     * @return the user's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the user.
     *
     * @param firstName the user's first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name of the user.
     *
     * @return the user's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the user.
     *
     * @param lastName the user's last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}