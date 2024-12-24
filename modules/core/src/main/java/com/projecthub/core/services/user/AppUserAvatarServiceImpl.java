package com.projecthub.core.services.user;

import com.projecthub.core.entities.AppUser;
import com.projecthub.core.exceptions.ResourceNotFoundException;
import com.projecthub.core.exceptions.StorageException;
import com.projecthub.core.repositories.jpa.AppUserJpaRepository;
import com.projecthub.core.services.user.AppUserProfileValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class AppUserAvatarServiceImpl implements AppUserAvatarService {
    private static final Logger logger = LoggerFactory.getLogger(AppUserAvatarServiceImpl.class);
    private static final String AVATAR_DIRECTORY = "avatars";

    private final AppUserJpaRepository appUserRepository;
    private final ProfileValidationService validationService;
    private final Path fileStorageLocation;

    public AppUserAvatarServiceImpl(
            AppUserJpaRepository appUserRepository,
            ProfileValidationService validationService,
            @Value("${app.file-storage-location}") String fileStorageLocation) {
        this.appUserRepository = appUserRepository;
        this.validationService = validationService;
        this.fileStorageLocation = Path.of(fileStorageLocation).resolve(AVATAR_DIRECTORY);
        createAvatarDirectory();
    }

    @Override
    @Transactional
    public void updateAvatar(UUID userId, MultipartFile avatar) {
        try {
            logger.info("Updating avatar for user with ID: {}", userId);
            validationService.validateAvatarFile(avatar);

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

    private void createAvatarDirectory() {
        try {
            Files.createDirectories(fileStorageLocation);
        } catch (IOException ex) {
            String errorMessage = "Could not create avatar directory at location: " + fileStorageLocation;
            logger.error(errorMessage, ex);
            throw new StorageException(errorMessage, ex);
        }
    }

    private String generateAvatarFilename(UUID userId, MultipartFile avatar) {
        String extension = getFileExtension(avatar.getOriginalFilename());
        return userId + "_" + System.currentTimeMillis() + extension;
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
        if (filename == null)
            return ".jpg";
        int lastDot = filename.lastIndexOf('.');
        return (lastDot > -1) ? filename.substring(lastDot) : ".jpg";
    }

    private AppUser findUserById(UUID id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }
}
