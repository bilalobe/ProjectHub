package com.projecthub.base.user.application.profile.service;

import com.projecthub.base.repository.jpa.AppUserJpaRepository;
import com.projecthub.base.shared.exception.ProfileUpdateException;
import com.projecthub.base.shared.exception.ResourceNotFoundException;
import com.projecthub.base.user.api.validation.AppUserProfileValidationService;
import com.projecthub.base.user.domain.entity.AppUser;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AppUserStatusServiceImpl implements AppUserStatusService {
    private static final Logger logger = LoggerFactory.getLogger(AppUserStatusServiceImpl.class);

    private final AppUserJpaRepository appUserRepository;
    private final AppUserProfileValidationService validationService;

    public AppUserStatusServiceImpl(
        final AppUserJpaRepository appUserRepository,
        final AppUserProfileValidationService validationService) {
        this.appUserRepository = appUserRepository;
        this.validationService = validationService;
    }

    @Override
    @Transactional
    public void updateStatus(final UUID userId, @NonNls final String status) {
        try {
            AppUserStatusServiceImpl.logger.info("Updating status for user with ID: {}", userId);
            this.validationService.validateStatus(status);

            final AppUser user = this.findUserById(userId);
            user.setStatusMessage(status.trim());
            this.appUserRepository.save(user);

            AppUserStatusServiceImpl.logger.info("Status updated successfully for user ID: {}", userId);
        } catch (final IllegalArgumentException ex) {
            AppUserStatusServiceImpl.logger.warn("Invalid status update attempt for user ID: {}", userId, ex);
            throw ex;
        } catch (final RuntimeException ex) {
            AppUserStatusServiceImpl.logger.error("Error updating status for user ID: {}", userId, ex);
            throw new ProfileUpdateException("Failed to update user status", ex);
        }
    }

    private AppUser findUserById(final UUID id) {
        return this.appUserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }
}
