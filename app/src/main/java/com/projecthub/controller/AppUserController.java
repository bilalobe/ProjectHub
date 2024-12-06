package com.projecthub.controller;

import com.projecthub.dto.AppUserDTO;
import com.projecthub.service.AppUserService;
import com.projecthub.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

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
    public ResponseEntity<String> createUser(@Valid @RequestBody AppUserDTO userDTO, @RequestParam String password) {
        logger.info("Creating a new user");
        userService.createUser(userDTO, password);
        return ResponseEntity.ok("User created successfully");
    }

    @Operation(summary = "Update a user")
    @PutMapping("/{id}")
    public ResponseEntity<AppUserDTO> updateUser(@PathVariable UUID id, @Valid @RequestBody AppUserDTO userDTO, @RequestParam(required = false) String password) {
        logger.info("Updating user with ID {}", id);
        AppUserDTO updatedUser = userService.updateUser(id, userDTO, password);
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
}