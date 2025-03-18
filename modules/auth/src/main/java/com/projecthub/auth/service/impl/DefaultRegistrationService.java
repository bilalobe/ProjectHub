package com.projecthub.auth.service.impl;

import com.projecthub.auth.api.dto.RegisterRequestDTO;
import com.projecthub.auth.service.PasswordService;
import com.projecthub.auth.service.RegistrationService;
import com.projecthub.base.user.service.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of the RegistrationService.
 * Coordinates between the auth module and foundation module's user profile management.
 */
@Service
public class DefaultRegistrationService implements RegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRegistrationService.class);
    private final UserProfileService userProfileService;
    private final PasswordService passwordService;

    public DefaultRegistrationService(
            UserProfileService userProfileService,
            PasswordService passwordService) {
        this.userProfileService = userProfileService;
        this.passwordService = passwordService;
    }

    @Override
    @Transactional
    public String registerUser(RegisterRequestDTO request) {
        logger.debug("Processing registration for user: {}", request.username());
        
        // Hash the user's password for secure storage
        String hashedPassword = passwordService.hash(request.password());
        
        // Create the user profile in the foundation module
        var user = userProfileService.createUserProfile(
                request.username(),
                request.email(),
                request.firstName(),
                request.lastName()
        );
        
        // TODO: Store the hashed password and associate with user
        // This would typically be done in a UserCredentialRepository
        
        logger.info("Successfully registered user: {}", request.username());
        return user.getId().toString();
    }

    @Override
    public boolean isUsernameTaken(String username) {
        logger.debug("Checking if username is taken: {}", username);
        return userProfileService.findByUsername(username).isPresent();
    }

    @Override
    public boolean isEmailTaken(String email) {
        logger.debug("Checking if email is taken: {}", email);
        return userProfileService.findByEmail(email).isPresent();
    }
}