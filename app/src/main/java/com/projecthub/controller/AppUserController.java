package com.projecthub.controller;

import com.projecthub.dto.AppUserDTO;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.service.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
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
 *   <li>{@link #createUser(AppUserDTO)} - Creates a new user.</li>
 *   <li>{@link #updateUser(UUID, AppUserDTO)} - Updates an existing user.</li>
 *   <li>{@link #deleteUser(UUID)} - Deletes a user by their ID.</li>
 * </ul>
 * 
 * <p>Exception Handling:</p>
 * <ul>
 *   <li>{@link #handleResourceNotFoundException(ResourceNotFoundException)} - Handles resource not found exceptions.</li>
 *   <li>{@link #handleException(Exception)} - Handles general exceptions.</li>
 * </ul>
 * 
 * @see AppUserService
 * @see AppUserDTO
 * @see ResourceNotFoundException
 */
@Tag(name = "User API", description = "Operations pertaining to users in ProjectHub")
@RestController
@RequestMapping("/users")
public class AppUserController {

    private static final Logger logger = LoggerFactory.getLogger(AppUserController.class);

    private final AppUserService userService;

    public AppUserController(AppUserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get all users")
    @GetMapping
    public ResponseEntity<List<AppUserDTO>> getAllUsers() {
        logger.info("Retrieving all users");
        List<AppUserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/{id}")
    public ResponseEntity<AppUserDTO> getUserById(@PathVariable UUID id) {
        logger.info("Retrieving user with ID {}", id);
        AppUserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Create a new user")
    @PostMapping
    public ResponseEntity<AppUserDTO> createUser(@Valid @RequestBody AppUserDTO userDTO) {
        logger.info("Creating a new user");
        String rawPassword = userDTO.getPassword(); // Extract raw password from DTO
        AppUserDTO createdUser = userService.createUser(userDTO, rawPassword);
        return ResponseEntity.ok(createdUser);
    }

    @Operation(summary = "Update a user")
    @PutMapping("/{id}")
    public ResponseEntity<AppUserDTO> updateUser(@PathVariable UUID id, @Valid @RequestBody AppUserDTO userDTO) {
        logger.info("Updating user with ID {}", id);
        String rawPassword = userDTO.getPassword(); // Extract raw password from DTO if provided
        AppUserDTO updatedUser = userService.updateUser(id, userDTO, rawPassword);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Delete a user by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID id) {
        logger.info("Deleting user with ID {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
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