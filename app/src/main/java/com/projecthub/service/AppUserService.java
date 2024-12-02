package com.projecthub.service;

import com.projecthub.dto.AppUserSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.mapper.AppUserMapper;
import com.projecthub.model.AppUser;
import com.projecthub.model.Team;
import com.projecthub.repository.AppUserRepository;
import com.projecthub.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppUserService {

    private static final Logger logger = LoggerFactory.getLogger(AppUserService.class);

    private final AppUserRepository appUserRepository;
    private final TeamRepository teamRepository;
    private final AppUserMapper appUserMapper;
    private final PasswordEncoder passwordEncoder;

    public AppUserService(AppUserRepository appUserRepository, TeamRepository teamRepository, AppUserMapper appUserMapper, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.teamRepository = teamRepository;
        this.appUserMapper = appUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AppUserSummary createUser(AppUserSummary userSummary, String password) {
        logger.info("Creating a new user");
        validateUserSummary(userSummary);

        Team team = teamRepository.findById(userSummary.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + userSummary.getTeamId()));

        String encodedPassword = passwordEncoder.encode(password);
        AppUser user = appUserMapper.toAppUser(userSummary, team, encodedPassword);
        AppUser savedUser = appUserRepository.save(user);

        logger.info("User created with ID {}", savedUser.getId());
        return appUserMapper.toAppUserSummary(savedUser);
    }

    @Transactional
    public AppUserSummary updateUser(Long id, AppUserSummary userSummary, String password) {
        logger.info("Updating user with ID {}", id);
        validateUserSummary(userSummary);

        AppUser existingUser = appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        Team team = teamRepository.findById(userSummary.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + userSummary.getTeamId()));

        existingUser.setUsername(userSummary.getUsername());
        if (password != null && !password.isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(password));
        }
        existingUser.setTeam(team);
        existingUser.setFirstName(userSummary.getFirstName());
        existingUser.setLastName(userSummary.getLastName());

        AppUser updatedUser = appUserRepository.save(existingUser);

        logger.info("User updated with ID {}", updatedUser.getId());
        return appUserMapper.toAppUserSummary(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        logger.info("Deleting user with ID {}", id);
        appUserRepository.deleteById(id);
        logger.info("User deleted with ID {}", id);
    }

    public AppUserSummary getUserById(Long id) {
        logger.info("Retrieving user with ID {}", id);
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return appUserMapper.toAppUserSummary(user);
    }

    public List<AppUserSummary> getAllUsers() {
        logger.info("Retrieving all users");
        return appUserRepository.findAll().stream()
                .map(appUserMapper::toAppUserSummary)
                .collect(Collectors.toList());
    }

    private void validateUserSummary(AppUserSummary userSummary) {
        if (userSummary == null) {
            throw new IllegalArgumentException("UserSummary cannot be null");
        }
        if (userSummary.getUsername() == null || userSummary.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (userSummary.getTeamId() == null) {
            throw new IllegalArgumentException("Team ID cannot be null");
        }
    }
}