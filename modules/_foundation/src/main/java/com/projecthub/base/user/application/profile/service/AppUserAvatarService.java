package com.projecthub.base.user.application.profile.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface AppUserAvatarService {
    void updateAvatar(UUID userId, MultipartFile avatar);
}
