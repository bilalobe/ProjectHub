package com.projecthub.base.user.application.profile.validation;

import com.projecthub.base.shared.exception.ValidationException;
import com.projecthub.base.user.api.validation.AppUserProfileValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.regex.Pattern;

@Service
public class AppUserProfileValidationServiceImpl implements AppUserProfileValidationService {
    private static final Logger logger = LoggerFactory.getLogger(AppUserProfileValidationServiceImpl.class);
    private static final int MAX_STATUS_LENGTH = 500;
    private static final long MAX_AVATAR_SIZE = (long) (5 * 1024 * 1024); // 5MB
    private static final Pattern PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public AppUserProfileValidationServiceImpl() {
    }

    @Override
    public void validateStatus(final String status) {
        try {
            AppUserProfileValidationServiceImpl.logger.debug("Validating status update: {}", status);
            if (!StringUtils.hasText(status)) {
                throw new ValidationException("Status cannot be empty");
            }
            if (MAX_STATUS_LENGTH < status.length()) {
                throw new ValidationException("Status message cannot exceed " + AppUserProfileValidationServiceImpl.MAX_STATUS_LENGTH + " characters");
            }
        } catch (final IllegalArgumentException ex) {
            AppUserProfileValidationServiceImpl.logger.error("Status validation failed", ex);
            throw new ValidationException("Invalid status format: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void validateAvatarFile(final MultipartFile avatar) {
        try {
            AppUserProfileValidationServiceImpl.logger.debug("Validating avatar file: {}", avatar.getOriginalFilename());
            if (null == avatar || avatar.isEmpty()) {
                throw new ValidationException("Avatar file cannot be empty");
            }
            if (MAX_AVATAR_SIZE < avatar.getSize()) {
                throw new ValidationException("Avatar file size cannot exceed 5MB");
            }
            final String contentType = avatar.getContentType();
            if (null == contentType || !contentType.startsWith("image/")) {
                throw new ValidationException("Invalid image file format");
            }
        } catch (final IllegalArgumentException ex) {
            AppUserProfileValidationServiceImpl.logger.error("Avatar validation failed", ex);
            throw new ValidationException("Invalid avatar file: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void validateProfileUpdate(final String firstName, final String lastName, final String email) {
        try {
            AppUserProfileValidationServiceImpl.logger.debug("Validating profile update for firstName: {}, lastName: {}, email: {}",
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
            if (!AppUserProfileValidationServiceImpl.isValidEmail(email)) {
                throw new ValidationException("Invalid email format");
            }
        } catch (final IllegalArgumentException ex) {
            AppUserProfileValidationServiceImpl.logger.error("Profile update validation failed", ex);
            throw new ValidationException("Invalid profile data: " + ex.getMessage(), ex);
        }
    }

    private static boolean isValidEmail(final String email) {
        return null != email && PATTERN.matcher(email).matches();
    }
}
