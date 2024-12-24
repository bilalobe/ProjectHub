package com.projecthub.core.dto;

public record CreateUserDTO(
    String username,
    String password,
    String email,
    String firstName,
    String lastName
) {}