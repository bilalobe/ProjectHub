package com.projecthub.core.services.user;

import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

public interface AppUserAvatarService {
    void updateAvatar(UUID userId, MultipartFile avatar);
}
