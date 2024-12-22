package com.projecthub.core.services.user;

import com.projecthub.core.dto.AppUserProfileDTO;
import com.projecthub.core.dto.UpdateUserRequestDTO;
import com.projecthub.core.entities.AppUser;
import com.projecthub.core.exceptions.ProfileUpdateException;
import com.projecthub.core.exceptions.ResourceNotFoundException;
import com.projecthub.core.exceptions.StorageException;
import com.projecthub.core.mappers.AppUserProfileMapper;
import com.projecthub.core.repositories.jpa.AppUserJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Service class for managing user profiles. Provides functionalities to update
 * and retrieve user profiles.
 */
@Service
public class AppUserProfileService {

    private static final Logger logger = LoggerFactory.getLogger(AppUserProfileService.class);
    private static final String AVATAR_DIRECTORY = "avatars";
    private final AppUserJpaRepository appUserRepository;
    private final AppUserProfileMapper userProfileMapper;
    private final Path fileStorageLocation;

    public AppUserProfileService(AppUserJpaRepository appUserRepository,
                                 AppUserProfileMapper userProfileMapper,
                                 @Value("${app.file-storage-location}") String fileStorageLocation) {
        this.appUserRepository = appUserRepository;
        this.userProfileMapper = userProfileMapper;
        this.fileStorageLocation = Path.of(fileStorageLocation).resolve(AVATAR_DIRECTORY);
        createAvatarDirectory();
    }

    private void createAvatarDirectory() {
        try {
            Files.createDirectories(fileStorageLocation);
        } catch (IOException ex) {
            String errorMessage = "Could not create avatar directory at location: " + fileStorageLocation;
            logger.error(errorMessage, ex);
            throw new StorageException(errorMessage, ex);
        }
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
     * @param id            the UUID of the user to update
     * @param updateRequest the data transfer object containing updated profile
     *                      details
     * @return the updated UserProfileDTO
     * @throws ResourceNotFoundException if the user with the specified ID does not
     *                                   exist
     */
    @Transactional
    public AppUserProfileDTO updateUserProfile(UUID id, UpdateUserRequestDTO updateRequest) {
        try {
            logger.info("Updating user profile with ID: {}", id);
            AppUser existingUser = findUserById(id);
            validateUpdateRequest(updateRequest);

            userProfileMapper.updateUserFromRequest(updateRequest, existingUser);
            AppUser updatedUser = appUserRepository.save(existingUser);

            logger.info("User profile updated with ID: {}", updatedUser.getId());
            return userProfileMapper.toUserProfileDTO(updatedUser);
        } catch (ResourceNotFoundException ex) {
            logger.error("Failed to find user with ID: {}", id);
            throw ex;
        } catch (Exception ex) {
            logger.error("Error updating profile for user ID: {}", id, ex);
            throw new ProfileUpdateException("Failed to update user profile", ex);
        }
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

    @Transactional
    public void updateAvatar(UUID userId, MultipartFile avatar) {
        try {
            logger.info("Updating avatar for user with ID: {}", userId);
            validateAvatarFile(avatar);

            AppUser user = findUserById(userId);
            String filename = generateAvatarFilename(userId, avatar);
            saveAvatarFile(avatar, filename);
            updateUserAvatar(user, filename);

            logger.info("Avatar updated successfully for user ID: {}", userId);
        } catch (IOException ex) {
            logger.error("Failed to store avatar file for user ID: {}", userId, ex);
            throw new StorageException("Failed to store avatar file", ex);
        }
    }

    @Transactional
    public void updateStatus(UUID userId, String status) {
        try {
            logger.info("Updating status for user with ID: {}", userId);
            validateStatus(status);

            AppUser user = findUserById(userId);
            user.setStatusMessage(status.trim());
            appUserRepository.save(user);

            logger.info("Status updated successfully for user ID: {}", userId);
        } catch (IllegalArgumentException ex) {
            logger.warn("Invalid status update attempt for user ID: {}", userId);
            throw ex;
        } catch (Exception ex) {
            logger.error("Error updating status for user ID: {}", userId, ex);
            throw new ProfileUpdateException("Failed to update user status", ex);
        }
    }

    private void validateAvatarFile(MultipartFile avatar) {
        if (avatar == null || avatar.isEmpty()) {
            throw new IllegalArgumentException("Avatar file cannot be empty");
        }
        if (!isValidImageFile(avatar)) {
            throw new IllegalArgumentException("Invalid image file format");
        }
    }

    private boolean isValidImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    private void validateStatus(String status) {
        if (!StringUtils.hasText(status)) {
            throw new IllegalArgumentException("Status cannot be empty");
        }
        if (status.length() > 500) {
            throw new IllegalArgumentException("Status message cannot exceed 500 characters");
        }
    }

    private void validateUpdateRequest(UpdateUserRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Update request cannot be null");
        }
        // Add more specific validations as needed
    }

    private String generateAvatarFilename(UUID userId, MultipartFile avatar) {
        return userId + "_" + System.currentTimeMillis() + getFileExtension(avatar.getOriginalFilename());
    }

    private void saveAvatarFile(MultipartFile avatar, String filename) throws IOException {
        Path targetLocation = fileStorageLocation.resolve(filename);
        Files.copy(avatar.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
    }

    private void updateUserAvatar(AppUser user, String filename) {
        user.setAvatarUrl("/avatars/" + filename);
        appUserRepository.save(user);
    }

    private String getFileExtension(String filename) {
        if (filename == null) return ".jpg";
        int lastDot = filename.lastIndexOf('.');
        return (lastDot > -1) ? filename.substring(lastDot) : ".jpg";
    }
}