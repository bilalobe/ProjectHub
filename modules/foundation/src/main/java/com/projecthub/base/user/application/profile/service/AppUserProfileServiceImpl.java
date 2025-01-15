package com.projecthub.base.user.application.profile.service;


import com.projecthub.base.repository.jpa.AppUserJpaRepository;
import com.projecthub.base.shared.exception.ProfileUpdateException;
import com.projecthub.base.shared.exception.ResourceNotFoundException;
import com.projecthub.base.user.api.dto.AppUserProfileDTO;
import com.projecthub.base.user.api.dto.UpdateUserRequestDTO;
import com.projecthub.base.user.api.mapper.AppUserProfileMapper;
import com.projecthub.base.user.api.validation.AppUserProfileValidationService;
import com.projecthub.base.user.domain.entity.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AppUserProfileServiceImpl implements AppUserProfileService {
    private static final Logger logger = LoggerFactory.getLogger(AppUserProfileServiceImpl.class);

    private final AppUserJpaRepository appUserRepository;
    private final AppUserProfileMapper userProfileMapper;
    private final AppUserProfileValidationService validationService;

    public AppUserProfileServiceImpl(
        final AppUserJpaRepository appUserRepository,
        final AppUserProfileMapper userProfileMapper,
        final AppUserProfileValidationService validationService) {
        this.appUserRepository = appUserRepository;
        this.userProfileMapper = userProfileMapper;
        this.validationService = validationService;
    }

    @Override
    public AppUserProfileDTO getUserProfileById(final UUID id) {
        AppUserProfileServiceImpl.logger.info("Retrieving user profile with ID: {}", id);
        final AppUser user = this.findUserById(id);
        return this.userProfileMapper.toDto(user);
    }

    @Override
    @Transactional
    public AppUserProfileDTO updateUserProfile(final UUID id, final UpdateUserRequestDTO updateRequest) {
        try {
            AppUserProfileServiceImpl.logger.info("Updating user profile with ID: {}", id);
            final AppUser existingUser = this.findUserById(id);
            this.validationService.validateUpdateRequest(updateRequest);

            this.userProfileMapper.updateEntityFromRequest(updateRequest, existingUser);
            final AppUser updatedUser = this.appUserRepository.save(existingUser);

            AppUserProfileServiceImpl.logger.info("User profile updated with ID: {}", updatedUser.getId());
            return this.userProfileMapper.toDto(updatedUser);
        } catch (final ResourceNotFoundException ex) {
            AppUserProfileServiceImpl.logger.error("Failed to find user with ID: {}", id, ex);
            throw ex;
        } catch (final Exception ex) {
            AppUserProfileServiceImpl.logger.error("Error updating profile for user ID: {}", id, ex);
            throw new ProfileUpdateException("Failed to update user profile", ex);
        }
    }

    private AppUser findUserById(final UUID id) {
        return this.appUserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }
}
