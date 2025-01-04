package com.projecthub.base.user.application.profile.validation;

import com.projecthub.base.shared.exception.ValidationException;
import com.projecthub.base.user.api.validation.AppUserProfileValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AppUserProfileValidationServiceImpl implements AppUserProfileValidationService {
    private static final Logger logger = LoggerFactory.getLogger(AppUserProfileValidationServiceImpl.class);
    private static final int MAX_STATUS_LENGTH = 500;
    private static final long MAX_AVATAR_SIZE = 5 * 1024 * 1024; // 5MB

    @Override
    public void validateStatus(String status) {
        try {
            logger.debug("Validating status update: {}", status);
            if (!StringUtils.hasText(status)) {
                throw new ValidationException("Status cannot be empty");
            }
            if (status.length() > MAX_STATUS_LENGTH) {
                throw new ValidationException("Status message cannot exceed " + MAX_STATUS_LENGTH + " characters");
            }
        } catch (IllegalArgumentException ex) {
            logger.error("Status validation failed", ex);
            throw new ValidationException("Invalid status format: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void validateAvatarFile(MultipartFile avatar) {
        try {
            logger.debug("Validating avatar file: {}", avatar.getOriginalFilename());
            if (avatar == null || avatar.isEmpty()) {
                throw new ValidationException("Avatar file cannot be empty");
            }
            if (avatar.getSize() > MAX_AVATAR_SIZE) {
                throw new ValidationException("Avatar file size cannot exceed 5MB");
            }
            String contentType = avatar.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new ValidationException("Invalid image file format");
            }
        } catch (IllegalArgumentException ex) {
            logger.error("Avatar validation failed", ex);
            throw new ValidationException("Invalid avatar file: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void validateProfileUpdate(String firstName, String lastName, String email) {
        try {
            logger.debug("Validating profile update for firstName: {}, lastName: {}, email: {}",
                firstName, lastName, email);

            if (!StringUtils.hasText(firstName)) {
                throw new ValidationException("First name cannot be empty");
            }
            if (!StringUtils.hasText(lastName)) {
                throw new ValidationException("Last name cannot be empty");
            }
            if (!StringUtils.hasText(email)) {
                throw new ValidationException("Email cannot be empty");
            }
            if (!isValidEmail(email)) {
                throw new ValidationException("Invalid email format");
            }
        } catch (IllegalArgumentException ex) {
            logger.error("Profile update validation failed", ex);
            throw new ValidationException("Invalid profile data: " + ex.getMessage(), ex);
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
