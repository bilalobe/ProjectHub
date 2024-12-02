package com.projecthub.controller;

import com.projecthub.dto.AppUserSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.service.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User API", description = "Operations pertaining to users in ProjectHub")
@RestController
@RequestMapping("/users")
public class AppUserController {

    @Autowired
    private AppUserService userService;

    @Operation(summary = "Get all users")
    @GetMapping
    public List<AppUserSummary> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/{id}")
    public ResponseEntity<AppUserSummary> getUserById(@PathVariable Long id) {
        try {
            AppUserSummary user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @Operation(summary = "Create a new user")
    @PostMapping
    public ResponseEntity<String> createUser(@Valid @RequestBody AppUserSummary userSummary, @RequestParam String password) {
        userService.createUser(userSummary, password);
        return ResponseEntity.ok("User created successfully");
    }

    @Operation(summary = "Update a user")
    @PutMapping("/{id}")
    public ResponseEntity<AppUserSummary> updateUser(@PathVariable Long id, @Valid @RequestBody AppUserSummary userSummary, @RequestParam(required = false) String password) {
        try {
            AppUserSummary updatedUser = userService.updateUser(id, userSummary, password);
            return ResponseEntity.ok(updatedUser);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @Operation(summary = "Delete a user by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body("User not found");
        }
    }
}