package com.projecthub.base.user.api.dto;

public record CreateUserDTO(
    String username,
    String password,
    String email,
    String firstName,
    String lastName
) {
}
