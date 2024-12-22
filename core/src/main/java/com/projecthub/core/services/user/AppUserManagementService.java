package com.projecthub.core.services.user;

import com.projecthub.core.dto.AppUserDTO;
import com.projecthub.core.entities.AppUser;
import com.projecthub.core.exceptions.ResourceNotFoundException;
import com.projecthub.core.mappers.AppUserMapper;
import com.projecthub.core.repositories.jpa.AppUserJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service class for managing users. Provides functionalities to retrieve, update, and delete users.
 */
@Service
public class AppUserManagementService {

    private static final Logger logger = LoggerFactory.getLogger(AppUserManagementService.class);

    private final AppUserJpaRepository appUserRepository;
    private final AppUserMapper userMapper;

    public AppUserManagementService(AppUserJpaRepository appUserRepository, AppUserMapper userMapper) {
        this.appUserRepository = appUserRepository;
        this.userMapper = userMapper;
    }

    /**
     * Retrieves all users.
     *
     * @return a list of AppUserDTOs
     */
    public List<AppUserDTO> getAllUsers() {
        logger.info("Retrieving all users");
        return appUserRepository.findAll().stream()
                .map(userMapper::toAppUserDTO)
                .toList();
    }

    /**
     * Deletes a user by their UUID.
     *
     * @param id the UUID of the user to delete
     * @throws ResourceNotFoundException if the user with the specified ID does not exist
     */
    @Transactional
    public void deleteUser(UUID id) {
        logger.info("Deleting user with ID: {}", id);
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        appUserRepository.delete(user);
        logger.info("User deleted with ID: {}", id);
    }
}