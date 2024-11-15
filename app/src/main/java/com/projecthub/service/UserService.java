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

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Service
@Tag(name = "User Service", description = "Operations pertaining to users in ProjectHub")
public class UserService {

    private final CustomUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(@Qualifier("postgresUserRepository") CustomUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "View a list of all users")
    @PreAuthorize("hasRole('USER')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Operation(summary = "Find user by ID")
    @PreAuthorize("hasRole('USER')")
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Operation(summary = "Save a user")
    @PreAuthorize("hasRole('USER')")
    public User saveUser(UserSummary userDTO) {
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return userRepository.save(userDTO);
    }

    @Operation(summary = "Delete a user by ID")
    @PreAuthorize("hasRole('USER')")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Operation(summary = "Get user summary")
    @PreAuthorize("hasRole('USER')")
    public UserSummary getUserSummary(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserSummary(
            user.getId(),
            user.getUsername(),
            user.getPassword(),
            user.getTeam() != null ? user.getTeam().getId() : null
        );
    }

    @Operation(summary = "Get team members summary")
    @PreAuthorize("hasRole('USER')")
    public List<UserSummary> getTeamMembersSummary(Long teamId) {
        return userRepository.findByTeamId(teamId)
            .stream()
            .map(user -> new UserSummary(
                user.getId(),
                user.getUsername(), 
                user.getPassword(),
                user.getTeam() != null ? user.getTeam().getId() : null
            ))
            .collect(Collectors.toList());
    }
}