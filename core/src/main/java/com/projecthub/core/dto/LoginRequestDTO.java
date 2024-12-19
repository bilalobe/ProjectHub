package com.projecthub.core.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user login requests.
 *
 * <p>
 * This DTO captures the necessary information required for user authentication,
 * including the username, password, remember-me preference, and the client's IP address.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    /**
     * The username of the user attempting to log in.
     *
     * <p>
     * This field is mandatory and must not be blank.
     * </p>
     */
    @NotBlank(message = "Username is mandatory")
    private String username;

    /**
     * The password of the user attempting to log in.
     *
     * <p>
     * This field is mandatory and must not be blank.
     * </p>
     */
    @NotBlank(message = "Password is mandatory")
    private String password;

    /**
     * Indicates whether the user has opted to be remembered on the device.
     *
     * <p>
     * If set to {@code true}, a persistent token will be generated to keep the user logged in.
     * </p>
     */
    private boolean rememberMe;

    /**
     * The IP address of the client making the login request.
     *
     * <p>
     * This field can be used for auditing and security purposes.
     * </p>
     */
    private String ipAddress;
}