package com.projecthub.dto;

import com.opencsv.bean.CsvBindByName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object for the School entity.
 * Used for transferring school data between processes.
 */
public class SchoolDTO {

    /**
     * The unique identifier of the school.
     */
    @CsvBindByName(column = "id")
    @NotNull(message = "School ID cannot be null")
    private UUID id;

    /**
     * The name of the school.
     */
    @CsvBindByName(column = "name")
    @NotBlank(message = "School name is mandatory")
    private String name;

    /**
     * The address of the school.
     */
    @CsvBindByName(column = "address")
    @Size(max = 255, message = "Address must be less than 255 characters")
    private String address;

    /**
     * The list of team IDs associated with the school.
     */
    @CsvBindByName(column = "teamIds")
    private List<UUID> teamIds;

    /**
     * The list of cohort IDs associated with the school.
     */
    @CsvBindByName(column = "cohortIds")
    private List<UUID> cohortIds;

    /**
     * The date and time when the school was created.
     */
    @CsvBindByName(column = "createdAt")
    private LocalDateTime createdAt;

    /**
     * The date and time when the school was last updated.
     */
    @CsvBindByName(column = "updatedAt")
    private LocalDateTime updatedAt;

    /**
     * The user who created the school entry.
     */
    @CsvBindByName(column = "createdBy")
    private String createdBy;

    /**
     * Indicates whether the school is deleted.
     */
    @CsvBindByName(column = "deleted")
    private boolean deleted;

    // Default constructor
    public SchoolDTO() {}

    /**
     * Constructs a new SchoolDTO with the specified details.
     *
     * @param id         the unique identifier of the school
     * @param name       the name of the school
     * @param address    the address of the school
     * @param teamIds    the list of team IDs associated with the school
     * @param cohortIds  the list of cohort IDs associated with the school
     * @param createdAt  the date and time when the school was created
     * @param updatedAt  the date and time when the school was last updated
     * @param createdBy  the user who created the school entry
     * @param deleted    indicates whether the school is deleted
     */
    public SchoolDTO(UUID id, String name, String address, List<UUID> teamIds, List<UUID> cohortIds, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, boolean deleted) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.teamIds = teamIds;
        this.cohortIds = cohortIds;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.deleted = deleted;
    }

    /**
     * Gets the unique identifier of the school.
     *
     * @return the school's unique identifier
     */
    public UUID getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the school.
     *
     * @param id the school's unique identifier
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Gets the name of the school.
     *
     * @return the school's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the school.
     *
     * @param name the school's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the address of the school.
     *
     * @return the school's address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address of the school.
     *
     * @param address the school's address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the list of team IDs associated with the school.
     *
     * @return the list of team IDs
     */
    public List<UUID> getTeamIds() {
        return teamIds;
    }

    /**
     * Sets the list of team IDs associated with the school.
     *
     * @param teamIds the list of team IDs
     */
    public void setTeamIds(List<UUID> teamIds) {
        this.teamIds = teamIds;
    }

    /**
     * Gets the list of cohort IDs associated with the school.
     *
     * @return the list of cohort IDs
     */
    public List<UUID> getCohortIds() {
        return cohortIds;
    }

    /**
     * Sets the list of cohort IDs associated with the school.
     *
     * @param cohortIds the list of cohort IDs
     */
    public void setCohortIds(List<UUID> cohortIds) {
        this.cohortIds = cohortIds;
    }

    /**
     * Gets the date and time when the school was created.
     *
     * @return the creation date and time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the date and time when the school was created.
     *
     * @param createdAt the creation date and time
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the date and time when the school was last updated.
     *
     * @return the last update date and time
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the date and time when the school was last updated.
     *
     * @param updatedAt the last update date and time
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Gets the user who created the school entry.
     *
     * @return the creator of the school entry
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the user who created the school entry.
     *
     * @param createdBy the creator of the school entry
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Indicates whether the school is deleted.
     *
     * @return true if the school is deleted, false otherwise
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Sets the deletion status of the school.
     *
     * @param deleted the deletion status
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}