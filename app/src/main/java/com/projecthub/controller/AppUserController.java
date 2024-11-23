package com.projecthub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projecthub.dto.AppUserSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.service.UserService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class AppUserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<AppUserSummary> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public String createUser(@Valid @RequestBody AppUserSummary userSummary) {
        userService.saveUser(userSummary);
        return "User created successfully";
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) throws ResourceNotFoundException {
        userService.deleteUser(id);
        return "User deleted successfully";
    }
}