package com.projecthub.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a user in the system.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
public class AppUser {

    /**
     * The unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * The timestamp when the user was created.
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * The timestamp when the user was last updated.
     */
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * The user who created this user.
     */
    @CreatedBy
    private String createdBy;

    /**
     * The user's username.
     */
    @NotBlank(message = "Username is mandatory")
    private String username;

    /**
     * The user's password.
     */
    @NotBlank(message = "Password is mandatory")
    private String password;

    /**
     * The user's first name.
     */
    @NotBlank(message = "First name is mandatory")
    private String firstName;

    /**
     * The user's last name.
     */
    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    /**
     * The user's email.
     */
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    /**
     * The team that the user belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    private boolean deleted = false;

    /**
     * Default constructor required by JPA.
     */
    public AppUser() {
    }

    /**
     * Constructs a new user with the specified fields.
     *
     * @param username  the user's username
     * @param password  the user's password
     * @param firstName the user's first name
     * @param lastName  the user's last name
     * @param email     the user's email
     * @param team      the team the user belongs to
     */
    public AppUser(String username, String password, String firstName, String lastName, String email, Team team) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.team = team;
    }

    // Getters and setters

    /**
     * Returns the user's ID.
     *
     * @return the user's ID
     */
    public UUID getId() {
        return id;
    }

    // No setter for ID to prevent manual setting

    /**
     * Returns the user's username.
     *
     * @return the user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the user's username.
     *
     * @param username the user's username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the user's password.
     *
     * @return the user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     *
     * @param password the user's password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the user's first name.
     *
     * @return the user's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the user's first name.
     *
     * @param firstName the user's first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the user's last name.
     *
     * @return the user's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the user's last name.
     *
     * @param lastName the user's last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the user's email.
     *
     * @return the user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email.
     *
     * @param email the user's email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the team that the user belongs to.
     *
     * @return the team
     */
    public Team getTeam() {
        return team;
    }

    /**
     * Sets the team that the user belongs to.
     *
     * @param team the team
     */
    public void setTeam(Team team) {
        this.team = team;
    }

    /**
     * Returns the timestamp when the user was created.
     *
     * @return the createdAt timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Returns the timestamp when the user was last updated.
     *
     * @return the updatedAt timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Returns the user who created this user.
     *
     * @return the createdBy user
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Returns whether the user is marked as deleted.
     *
     * @return true if the user is deleted, false otherwise
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Sets whether the user is marked as deleted.
     *
     * @param deleted true if the user is deleted, false otherwise
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Returns a string representation of the user.
     *
     * @return a string containing the user's ID and username
     */
    @Override
    public String toString() {
        return "AppUser{id=" + id + ", username='" + username + "', email='" + email + "', firstName='" + firstName + "', lastName='" + lastName + "', team=" + team + "}";
    }
}