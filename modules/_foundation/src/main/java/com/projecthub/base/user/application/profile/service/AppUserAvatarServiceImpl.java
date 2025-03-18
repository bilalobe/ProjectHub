package com.projecthub.base.user.application.profile.service;

import com.projecthub.base.repository.jpa.AppUserJpaRepository;
import com.projecthub.base.shared.exception.ResourceNotFoundException;
import com.projecthub.base.shared.exception.StorageException;
import com.projecthub.base.user.api.validation.AppUserProfileValidationService;
import com.projecthub.base.user.domain.entity.AppUser;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamSource;
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
    private final AppUserProfileValidationService validationService;
    private final Path fileStorageLocation;

    public AppUserAvatarServiceImpl(
        final AppUserJpaRepository appUserRepository,
        final AppUserProfileValidationService validationService,
        @Value("${app.file-storage-location}") final String fileStorageLocation) {
        this.appUserRepository = appUserRepository;
        this.validationService = validationService;
        this.fileStorageLocation = Path.of(fileStorageLocation).resolve(AppUserAvatarServiceImpl.AVATAR_DIRECTORY);
        this.createAvatarDirectory();
    }

    @Override
    @Transactional
    public void updateAvatar(final UUID userId, final MultipartFile avatar) {
        try {
            AppUserAvatarServiceImpl.logger.info("Updating avatar for user with ID: {}", userId);
            this.validationService.validateAvatarFile(avatar);

            final AppUser user = this.findUserById(userId);
            final String filename = this.generateAvatarFilename(userId, avatar);
            this.saveAvatarFile(avatar, filename);
            this.updateUserAvatar(user, filename);

            AppUserAvatarServiceImpl.logger.info("Avatar updated successfully for user ID: {}", userId);
        } catch (final IOException ex) {
            AppUserAvatarServiceImpl.logger.error("Failed to store avatar file for user ID: {}", userId, ex);
            throw new StorageException("Failed to store avatar file", ex);
        }
    }

    private void createAvatarDirectory() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (final IOException ex) {
            @NonNls final String errorMessage = "Could not create avatar directory at location: " + this.fileStorageLocation;
            AppUserAvatarServiceImpl.logger.error(errorMessage, ex);
            throw new StorageException(errorMessage, ex);
        }
    }

    private String generateAvatarFilename(final UUID userId, final MultipartFile avatar) {
        @NonNls @NonNls final String extension = AppUserAvatarServiceImpl.getFileExtension(avatar.getOriginalFilename());
        return userId + "_" + System.currentTimeMillis() + extension;
    }

    private void saveAvatarFile(final InputStreamSource avatar, final String filename) throws IOException {
        final Path targetLocation = this.fileStorageLocation.resolve(filename);
        Files.copy(avatar.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
    }

    private void updateUserAvatar(@NonNls final AppUser user, final String filename) {
        user.setAvatarUrl("/avatars/" + filename);
        this.appUserRepository.save(user);
    }

    private static String getFileExtension(final String filename) {
        if (null == filename)
            return ".jpg";
        final int lastDot = filename.lastIndexOf((int) '.');
        return (-1 < lastDot) ? filename.substring(lastDot) : ".jpg";
    }

    private AppUser findUserById(final UUID id) {
        return this.appUserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }
}
