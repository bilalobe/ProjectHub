package com.projecthub.core.dto;

import lombok.Data;

@Data
public class GithubUserInfo {
    private String login;
    private String email;
    private String name;
    private String avatarUrl;
}