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

    public AppUserController(final AppUserManagementService appUserService) {
        this.appUserService = appUserService;
    }

    @Override
    public ResponseEntity<?> getAllUsers() {
        AppUserController.logger.info("Retrieving all users");
        return ResponseEntity.ok(this.appUserService.getAllUsers());
    }

    @Override
    public ResponseEntity<AppUserDTO> getById(@PathVariable final UUID id) {
        AppUserController.logger.info("Retrieving user with ID {}", id);
        return ResponseEntity.ok(this.appUserService.getUserById(id));
    }

    @Override
    public ResponseEntity<AppUserDTO> save(@Valid @RequestBody final AppUserDTO user) {
        AppUserController.logger.info("Creating new user");
        return ResponseEntity.ok(this.appUserService.saveUser(user));
    }

    @Override
    public ResponseEntity<AppUserDTO> update(@PathVariable final UUID id, @Valid @RequestBody final AppUserDTO user) {
        AppUserController.logger.info("Updating user with ID {}", id);
        return ResponseEntity.ok(this.appUserService.updateUser(id, user));
    }

    @Override
    public ResponseEntity<Void> deleteById(@PathVariable final UUID id) {
        AppUserController.logger.info("Deleting user with ID {}", id);
        this.appUserService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<AppUserDTO> createUser(@Valid @RequestBody final RegisterRequestDTO registerRequest) {
        AppUserController.logger.info("Registering new user");
        return ResponseEntity.ok(this.appUserService.createUser(registerRequest));
    }
}
