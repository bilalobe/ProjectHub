package com.projecthub.base.user.api.dto;

/**
 * Data Transfer Object for updating user information in the ProjectHub system.
 * Contains all modifiable user attributes.
 *
 * @param username  User's login identifier
 * @param password  User's authentication credential (optional for updates)
 * @param email     User's contact email address
 * @param firstName User's given name
 * @param lastName  User's family name
 */
public record UpdateUserRequestDTO(
    String username,
    String password,
    String email,
    String firstName,
    String lastName,
    String statusMessage
) {
}
