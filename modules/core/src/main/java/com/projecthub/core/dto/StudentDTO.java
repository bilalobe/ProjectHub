package com.projecthub.core.dto;

import java.util.UUID;

/**
 * Data Transfer Object representing a student in the ProjectHub system.
 * Contains basic student information and team association.
 *
 * @param id Unique identifier of the student
 * @param email Student's contact email address
 * @param firstName Student's given name
 * @param lastName Student's family name
 * @param teamId Reference to the team the student belongs to (optional)
 */
public record StudentDTO(
    UUID id,
    String email,
    String firstName,
    String lastName,
    UUID teamId
) {}