package com.projecthub.base.auth.api.controller;

import com.projecthub.base.auth.api.dto.RoleDTO;
import com.projecthub.base.auth.api.dto.UserCreateDTO;
import com.projecthub.base.auth.api.dto.UserDTO;
import com.projecthub.base.auth.api.dto.UserUpdateDTO;
import com.projecthub.base.auth.service.fortress.FortressUserManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for user management operations through Fortress APIs.
 * Provides endpoints for managing users, roles, and permissions.
 */
@RestController
@RequestMapping("/api/v1/admin/users")
@Tag(name = "User Management", description = "Administrative APIs for user management")
@PreAuthorize("hasAnyFortressRole('ADMIN')")
public class UserManagementController {

    private static final Logger log = LoggerFactory.getLogger(UserManagementController.class);
    private final FortressUserManagementService userManagementService;

    public UserManagementController(FortressUserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<List<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Fetching all users (page={}, size={})", Integer.valueOf(page), Integer.valueOf(size));
        return ResponseEntity.ok(userManagementService.getAllUsers(page, size));
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get user by username")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        log.info("Fetching user: {}", username);
        return ResponseEntity.ok(userManagementService.getUserByUsername(username));
    }

    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        log.info("Creating new user: {}", userCreateDTO.username());
        return new ResponseEntity<>(userManagementService.createUser(userCreateDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{username}")
    @Operation(summary = "Update an existing user")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable String username,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        log.info("Updating user: {}", username);
        return ResponseEntity.ok(userManagementService.updateUser(username, userUpdateDTO));
    }

    @DeleteMapping("/{username}")
    @Operation(summary = "Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        log.info("Deleting user: {}", username);
        userManagementService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{username}/enable")
    @Operation(summary = "Enable a user account")
    public ResponseEntity<UserDTO> enableUser(@PathVariable String username) {
        log.info("Enabling user: {}", username);
        return ResponseEntity.ok(userManagementService.enableUser(username));
    }

    @PutMapping("/{username}/disable")
    @Operation(summary = "Disable a user account")
    public ResponseEntity<UserDTO> disableUser(@PathVariable String username) {
        log.info("Disabling user: {}", username);
        return ResponseEntity.ok(userManagementService.disableUser(username));
    }

    @PutMapping("/{username}/unlock")
    @Operation(summary = "Unlock a user account")
    public ResponseEntity<UserDTO> unlockUser(@PathVariable String username) {
        log.info("Unlocking user: {}", username);
        return ResponseEntity.ok(userManagementService.unlockUser(username));
    }

    @PutMapping("/{username}/lock")
    @Operation(summary = "Lock a user account")
    public ResponseEntity<UserDTO> lockUser(@PathVariable String username) {
        log.info("Locking user: {}", username);
        return ResponseEntity.ok(userManagementService.lockUser(username));
    }

    @GetMapping("/{username}/roles")
    @Operation(summary = "Get roles for a user")
    public ResponseEntity<List<RoleDTO>> getUserRoles(@PathVariable String username) {
        log.info("Fetching roles for user: {}", username);
        return ResponseEntity.ok(userManagementService.getUserRoles(username));
    }

    @PostMapping("/{username}/roles/{roleName}")
    @Operation(summary = "Assign a role to a user")
    public ResponseEntity<Void> assignRoleToUser(
            @PathVariable String username,
            @PathVariable String roleName) {
        log.info("Assigning role {} to user: {}", roleName, username);
        userManagementService.assignRoleToUser(username, roleName);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{username}/roles/{roleName}")
    @Operation(summary = "Remove a role from a user")
    public ResponseEntity<Void> removeRoleFromUser(
            @PathVariable String username,
            @PathVariable String roleName) {
        log.info("Removing role {} from user: {}", roleName, username);
        userManagementService.removeRoleFromUser(username, roleName);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/audit")
    @Operation(summary = "Get user audit logs")
    public ResponseEntity<List<Map<String, Object>>> getUserAuditLogs(
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Fetching user audit logs for: {}", username != null ? username : "all users");
        return ResponseEntity.ok(userManagementService.getUserAuditLogs(username, page, size));
    }
}
