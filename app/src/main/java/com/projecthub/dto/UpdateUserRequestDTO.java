package com.projecthub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object for updating user information.
 * This class is used to encapsulate the data required to update a user's details.
 * 
 * Fields:
 * - username: The username of the user. It is required and must be between 3 and 50 characters.
 * - password: The password of the user. It is optional but must be at least 6 characters long if provided.
 * - email: The email address of the user. It is required and must be a valid email format.
 * - firstName: The first name of the user. It is required and must not exceed 100 characters.
 * - lastName: The last name of the user. It is required and must not exceed 100 characters.
 * 
 * Annotations:
 * - @Data: Lombok annotation to generate getters, setters, toString, equals, and hashCode methods.
 * - @NotBlank: Ensures the field is not null and not empty.
 * - @Size: Specifies the size constraints for the field.
 * - @Email: Ensures the field contains a valid email address.
 */
@Data
public class UpdateUserRequestDTO {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    private String username;

    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password; // Optional

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "First name is required")
    @Size(max = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100)
    private String lastName;
}