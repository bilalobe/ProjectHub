package com.projecthub.core.services.user;

import com.projecthub.core.entities.AppUser;
import com.projecthub.core.exceptions.ProfileUpdateException;
import com.projecthub.core.exceptions.ResourceNotFoundException;
import com.projecthub.core.repositories.jpa.AppUserJpaRepository;
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
            AppUserJpaRepository appUserRepository,
            AppUserProfileValidationService validationService) {
        this.appUserRepository = appUserRepository;
        this.validationService = validationService;
    }

    @Override
    @Transactional
    public void updateStatus(UUID userId, String status) {
        try {
            logger.info("Updating status for user with ID: {}", userId);
            validationService.validateStatus(status);

            AppUser user = findUserById(userId);
            user.setStatusMessage(status.trim());
            appUserRepository.save(user);

            logger.info("Status updated successfully for user ID: {}", userId);
        } catch (IllegalArgumentException ex) {
            logger.warn("Invalid status update attempt for user ID: {}", userId, ex);
            throw ex;
        } catch (Exception ex) {
            logger.error("Error updating status for user ID: {}", userId, ex);
            throw new ProfileUpdateException("Failed to update user status", ex);
        }
    }

    private AppUser findUserById(UUID id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }
}
