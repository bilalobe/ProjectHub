package com.projecthub.core.services.user;

import com.projecthub.core.dto.AppUserProfileDTO;
import com.projecthub.core.exceptions.ResourceNotFoundException;
import com.projecthub.core.mappers.AppUserProfileMapper;
import com.projecthub.core.models.AppUser;
import com.projecthub.core.repositories.jpa.AppUserJpaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service class for managing user profiles. Provides functionalities to update
 * and retrieve user profiles.
 */
@Service
public class AppUserProfileService {

    private static final Logger logger = LoggerFactory.getLogger(AppUserProfileService.class);

    private final AppUserJpaRepository appUserRepository;
    private final AppUserProfileMapper userProfileMapper;

    public AppUserProfileService(AppUserJpaRepository appUserRepository, AppUserProfileMapper userProfileMapper) {
        this.appUserRepository = appUserRepository;
        this.userProfileMapper = userProfileMapper;
    }

    /**
     * Retrieves a user profile by their UUID.
     *
     * @param id the UUID of the user to retrieve
     * @return the UserProfileDTO of the retrieved user
     * @throws ResourceNotFoundException if the user with the specified ID does not
     *                                   exist
     */
    public AppUserProfileDTO getUserProfileById(UUID id) {
        logger.info("Retrieving user profile with ID: {}", id);
        AppUser user = findUserById(id);
        return userProfileMapper.toUserProfileDTO(user);
    }

    /**
     * Updates a user profile with the provided details.
     *
     * @param id             the UUID of the user to update
     * @param userProfileDTO the data transfer object containing updated profile
     *                       details
     * @return the updated UserProfileDTO
     * @throws ResourceNotFoundException if the user with the specified ID does not
     *                                   exist
     */
    @Transactional
    public AppUserProfileDTO updateUserProfile(UUID id, AppUserProfileDTO userProfileDTO) {
        logger.info("Updating user profile with ID: {}", id);
        AppUser existingUser = findUserById(id);

        userProfileMapper.updateUserProfileFromDTO(userProfileDTO, existingUser);
        AppUser updatedUser = appUserRepository.save(existingUser);

        logger.info("User profile updated with ID: {}", updatedUser.getId());
        return userProfileMapper.toUserProfileDTO(updatedUser);
    }

    /**
     * Finds a user by their UUID.
     *
     * @param id the UUID of the user to find
     * @return the AppUser entity
     * @throws ResourceNotFoundException if the user with the specified ID does not
     *                                   exist
     */
    private AppUser findUserById(UUID id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }
}