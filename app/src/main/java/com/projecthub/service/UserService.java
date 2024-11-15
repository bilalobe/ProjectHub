package com.projecthub.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projecthub.dto.UserSummary;
import com.projecthub.model.User;
import com.projecthub.repository.custom.CustomUserRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Service
@Api(value = "User Service", description = "Operations pertaining to users in ProjectHub")
public class UserService {

    private final CustomUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(@Qualifier("postgresUserRepository") CustomUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @ApiOperation(value = "View a list of all users", response = List.class)
    @PreAuthorize("hasRole('USER')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @ApiOperation(value = "Find user by ID")
    @PreAuthorize("hasRole('USER')")
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @ApiOperation(value = "Save a user")
    @PreAuthorize("hasRole('USER')")
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @ApiOperation(value = "Delete a user by ID")
    @PreAuthorize("hasRole('USER')")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @ApiOperation(value = "Get user summary", response = UserSummary.class)
    @PreAuthorize("hasRole('USER')")
    public UserSummary getUserSummary(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserSummary(user);
    }

    @ApiOperation(value = "Get team members summary", response = List.class)
    @PreAuthorize("hasRole('USER')")
    public List<UserSummary> getTeamMembersSummary(Long teamId) {
        return userRepository.findByTeamId(teamId)
            .stream()
            .map(UserSummary::new)
            .collect(Collectors.toList());
    }
}