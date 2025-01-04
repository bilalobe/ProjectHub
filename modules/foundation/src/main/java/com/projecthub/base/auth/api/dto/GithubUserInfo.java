package com.projecthub.base.auth.api.dto;

public record GithubUserInfo(
    String login,
    String email,
    String name,
    String avatarUrl
) {
}
