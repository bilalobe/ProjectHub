package com.projecthub.base.auth.api.dto;

public record RegisterInput(
    String username,
    String email,
    String password
) {
}
