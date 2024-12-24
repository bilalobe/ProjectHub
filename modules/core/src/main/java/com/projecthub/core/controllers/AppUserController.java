package com.projecthub.core.controllers;

import com.projecthub.core.api.AppUserApi;
import com.projecthub.core.dto.AppUserDTO;
import com.projecthub.core.dto.RegisterRequestDTO;
import com.projecthub.core.services.user.AppUserManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class AppUserController implements AppUserApi {

    private static final Logger logger = LoggerFactory.getLogger(AppUserController.class);
    private final AppUserManagementService appUserService;

    public AppUserController(AppUserManagementService appUserService) {
        this.appUserService = appUserService;
    }

    @Override
    public ResponseEntity<List<AppUserDTO>> getAllUsers() {
        logger.info("Retrieving all users");
        return ResponseEntity.ok(appUserService.getAllUsers());
    }

    @Override
    public ResponseEntity<AppUserDTO> getById(@PathVariable UUID id) {
        logger.info("Retrieving user with ID {}", id);
        return ResponseEntity.ok(appUserService.getUserById(id));
    }

    public ResponseEntity<AppUserDTO> save(@Valid @RequestBody AppUserDTO user) {
        logger.info("Creating new user");
        return ResponseEntity.ok(appUserService.saveUser(user));
    }

    public ResponseEntity<AppUserDTO> update(@PathVariable UUID id, @Valid @RequestBody AppUserDTO user) {
        logger.info("Updating user with ID {}", id);
        return ResponseEntity.ok(appUserService.updateUser(id, user));
    }

    @Override
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        logger.info("Deleting user with ID {}", id);
        appUserService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<AppUserDTO> createUser(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        logger.info("Registering new user");
        return ResponseEntity.ok(appUserService.createUser(registerRequest));
    }
}