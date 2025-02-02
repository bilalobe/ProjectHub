package com.projecthub.base.user.api.validation;

import org.springframework.web.multipart.MultipartFile;

public interface AppUserProfileValidationService {
    void validateStatus(String status);

    void validateAvatarFile(MultipartFile avatar);

    void validateProfileUpdate(String firstName, String lastName, String email);
}
