package com.projecthub.dto;

import com.opencsv.bean.CsvBindByName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Data Transfer Object for Student summary information.
 * Used for transferring student data between processes.
 */
public class StudentDTO {

    /**
     * Unique identifier for the student.
     */
    @CsvBindByName
    @NotNull(message = "Student ID cannot be null")
    private UUID id;

    /**
     * Email address of the student.
     */
    @CsvBindByName
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    private String email;

    /**
     * First name of the student.
     */
    @CsvBindByName
    @NotBlank(message = "First name is mandatory")
    @Size(max = 100, message = "First name must be less than 100 characters")
    private String firstName;

    /**
     * Last name of the student.
     */
    @CsvBindByName
    @NotBlank(message = "Last name is mandatory")
    @Size(max = 100, message = "Last name must be less than 100 characters")
    private String lastName;

    /**
     * Team identifier the student is assigned to.
     */
    @CsvBindByName
    private UUID teamId;

    /**
     * No-argument constructor.
     */
    public StudentDTO() {}

    /**
     * Constructor with all fields.
     *
     * @param id the student ID
     * @param email the student email
     * @param firstName the student first name
     * @param lastName the student last name
     * @param teamId the team ID
     */
    public StudentDTO(UUID id, String email, String firstName, String lastName, UUID teamId) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.teamId = teamId;
    }

    /**
     * Gets the unique identifier for the student.
     *
     * @return the student ID
     */
    public UUID getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the student.
     *
     * @param id the student ID
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Gets the email address of the student.
     *
     * @return the student email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the student.
     *
     * @param email the student email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the first name of the student.
     *
     * @return the student first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the student.
     *
     * @param firstName the student first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name of the student.
     *
     * @return the student last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the student.
     *
     * @param lastName the student last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the team identifier the student is assigned to.
     *
     * @return the team ID
     */
    public UUID getTeamId() {
        return teamId;
    }

    /**
     * Sets the team identifier the student is assigned to.
     *
     * @param teamId the team ID
     */
    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
    }
}