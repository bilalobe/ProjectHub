package com.projecthub.core.services.user;

import com.projecthub.core.dto.AppUserProfileDTO;
import com.projecthub.core.dto.UpdateUserRequestDTO;
import com.projecthub.core.entities.AppUser;
import com.projecthub.core.exceptions.ProfileUpdateException;
import com.projecthub.core.exceptions.ResourceNotFoundException;
import com.projecthub.core.mappers.AppUserProfileMapper;
import com.projecthub.core.repositories.jpa.AppUserJpaRepository;
import com.projecthub.core.services.validation.ProfileValidationService;
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
    private final ProfileValidationService validationService;

    public AppUserProfileServiceImpl(
            AppUserJpaRepository appUserRepository,
            AppUserProfileMapper userProfileMapper,
            ProfileValidationService validationService) {
        this.appUserRepository = appUserRepository;
        this.userProfileMapper = userProfileMapper;
        this.validationService = validationService;
    }

    @Override
    public AppUserProfileDTO getUserProfileById(UUID id) {
        logger.info("Retrieving user profile with ID: {}", id);
        AppUser user = findUserById(id);
        return userProfileMapper.toDto(user);
    }

    @Override
    @Transactional
    public AppUserProfileDTO updateUserProfile(UUID id, UpdateUserRequestDTO updateRequest) {
        try {
            logger.info("Updating user profile with ID: {}", id);
            AppUser existingUser = findUserById(id);
            validationService.validateUpdateRequest(updateRequest);

            userProfileMapper.updateEntityFromRequest(updateRequest, existingUser);
            AppUser updatedUser = appUserRepository.save(existingUser);

            logger.info("User profile updated with ID: {}", updatedUser.getId());
            return userProfileMapper.toDto(updatedUser);
        } catch (ResourceNotFoundException ex) {
            logger.error("Failed to find user with ID: {}", id, ex);
            throw ex;
        } catch (Exception ex) {
            logger.error("Error updating profile for user ID: {}", id, ex);
            throw new ProfileUpdateException("Failed to update user profile", ex);
        }
    }

    private AppUser findUserById(UUID id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }
}
