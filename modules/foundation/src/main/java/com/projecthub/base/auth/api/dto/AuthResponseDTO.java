package com.projecthub.base.auth.api.dto;

import com.projecthub.base.user.api.dto.AppUserDTO;

/**
 * Data Transfer Object for handling authentication responses.
 * Contains the authentication token and the authenticated user.
 *
 * @param token Authentication token
 * @param user  Authenticated user
 */
public record AuthResponseDTO(
    String token,
    AppUserDTO user
) {
}
