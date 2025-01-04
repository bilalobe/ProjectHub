package com.projecthub.base.user.api.controller;

import com.projecthub.base.auth.api.dto.RegisterRequestDTO;
import com.projecthub.base.user.api.dto.AppUserDTO;
import com.projecthub.base.user.api.rest.AppUserApi;
import com.projecthub.base.user.application.mgmt.service.AppUserManagementService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
public class AppUserController implements AppUserApi {

    private static final Logger logger = LoggerFactory.getLogger(AppUserController.class);
    private final AppUserManagementService appUserService;

    public AppUserController(AppUserManagementService appUserService) {
        this.appUserService = appUserService;
    }

    @Override
    public ResponseEntity<?> getAllUsers() {
        logger.info("Retrieving all users");
        return ResponseEntity.ok(appUserService.getAllUsers());
    }

    @Override
    public ResponseEntity<AppUserDTO> getById(@PathVariable UUID id) {
        logger.info("Retrieving user with ID {}", id);
        return ResponseEntity.ok(appUserService.getUserById(id));
    }

    @Override
    public ResponseEntity<AppUserDTO> save(@Valid @RequestBody AppUserDTO user) {
        logger.info("Creating new user");
        return ResponseEntity.ok(appUserService.saveUser(user));
    }

    @Override
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
