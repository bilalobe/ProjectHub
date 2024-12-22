package com.projecthub.core.controllers;

import com.projecthub.core.dto.AppUserDTO;
import com.projecthub.core.dto.RegisterRequestDTO;
import com.projecthub.core.dto.UpdateUserRequestDTO;
import com.projecthub.core.exceptions.ResourceNotFoundException;
import com.projecthub.core.services.user.AppUserManagementService;
import com.projecthub.core.services.user.AppUserProfileService;
import com.projecthub.core.services.user.AppUserRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller for handling user-related operations in ProjectHub.
 *
 * <p>This controller provides endpoints for creating, retrieving, updating, and deleting users.
 * It also includes exception handling for resource not found and general exceptions.</p>
 *
 * <ul>
 *   <li>{@link #getAllUsers()} - Retrieves all users.</li>
 *   <li>{@link #getUserById(UUID)} - Retrieves a user by their ID.</li>
 *   <li>{@link #createUser(RegisterRequestDTO)} - Creates a new user.</li>
 *   <li>{@link #updateUser(UUID, UpdateUserRequestDTO)} - Updates an existing user.</li>
 *   <li>{@link #deleteUser(UUID)} - Deletes a user by their ID.</li>
 * </ul>
 *
 * <p>Exception Handling:</p>
 * <ul>
 *   <li>{@link #handleResourceNotFoundException(ResourceNotFoundException)} - Handles resource not found exceptions.</li>
 *   <li>{@link #handleException(Exception)} - Handles general exceptions.</li>
 * </ul>
 *
 * @see AppUserRegistrationService
 * @see AppUserProfileService
 * @see AppUserManagementService
 * @see AppUserDTO
 * @see ResourceNotFoundException
 */
@Tag(name = "User API", description = "Operations pertaining to user management in ProjectHub")
@RestController
@RequestMapping("/api/v1/users")
public class AppUserController {

    private static final Logger logger = LoggerFactory.getLogger(AppUserController.class);

    private final AppUserRegistrationService userRegistrationService;
    private final AppUserManagementService userManagementService;

    public AppUserController(AppUserRegistrationService userRegistrationService,
                             AppUserManagementService userManagementService) {
        this.userRegistrationService = userRegistrationService;
        this.userManagementService = userManagementService;
    }

    @Operation(summary = "Get all users")
    @GetMapping
    public ResponseEntity<List<AppUserDTO>> getAllUsers() {
        logger.info("Retrieving all users");
        List<AppUserDTO> users = userManagementService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Create a new user")
    @PostMapping
    public ResponseEntity<AppUserDTO> createUser(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        logger.info("Creating a new user");
        AppUserDTO createdUser = userRegistrationService.registerUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @Operation(summary = "Delete a user by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        logger.info("Deleting user with ID {}", id);
        userManagementService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.error("Resource not found", ex);
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        logger.error("An error occurred", ex);
        return ResponseEntity.status(500).body("An internal error occurred. Please try again later.");
    }
}