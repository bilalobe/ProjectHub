package com.projecthub.core.dto;

public record GithubUserInfo(
    String login,
    String email,
    String name,
    String avatarUrl
) {}