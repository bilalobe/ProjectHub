package com.projecthub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.projecthub.dto.AppUserSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.service.UserService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Tag(name = "User API", description = "Operations pertaining to users in ProjectHub")
@RestController
@RequestMapping("/users")
public class AppUserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<AppUserSummary> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppUserSummary> getUserById(@PathVariable Long id) {
        Optional<AppUserSummary> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.status(404).body(null));
    }

    @Operation(summary = "Create a new user")
    @PostMapping
    public ResponseEntity<String> createUser(@Valid @RequestBody AppUserSummary userSummary) {
        userService.saveUser(userSummary, "defaultPassword"); // Assuming a default password
        return ResponseEntity.ok("User created successfully");
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