package com.projecthub.service;

import com.projecthub.dto.AppUserDTO;
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
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AppUserService {

    private static final Logger logger = LoggerFactory.getLogger(AppUserService.class);

    private final AppUserRepository appUserRepository;
    private final TeamRepository teamRepository;
    private final AppUserMapper appUserMapper;
    private final PasswordEncoder passwordEncoder;

    // Define a regex pattern for password strength validation
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
    );

    public AppUserService(AppUserRepository appUserRepository,
                          TeamRepository teamRepository,
                          AppUserMapper appUserMapper,
                          PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.teamRepository = teamRepository;
        this.appUserMapper = appUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AppUserDTO createUser(AppUserDTO userDTO, String rawPassword) {
        logger.info("Creating a new user");
        validateUserDTO(userDTO);
        validatePasswordStrength(rawPassword);

        Team team = findTeamById(userDTO.getTeamId());
        String encodedPassword = encodePassword(rawPassword);
        AppUser user = appUserMapper.toAppUser(userDTO, team, encodedPassword);
        AppUser savedUser = appUserRepository.save(user);

        logger.info("User created with ID: {}", savedUser.getId());
        return appUserMapper.toAppUserDTO(savedUser);
    }

    @Transactional
    public AppUserDTO updateUser(UUID id, AppUserDTO userDTO, String rawPassword) {
        logger.info("Updating user with ID: {}", id);
        validateUserDTO(userDTO);

        AppUser existingUser = findUserById(id);
        Team team = findTeamById(userDTO.getTeamId());

        existingUser.setUsername(userDTO.getUsername());
        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setTeam(team);

        if (rawPassword != null && !rawPassword.isEmpty()) {
            validatePasswordStrength(rawPassword);
            existingUser.setPassword(encodePassword(rawPassword));
        }

        AppUser updatedUser = appUserRepository.save(existingUser);

        logger.info("User updated with ID: {}", updatedUser.getId());
        return appUserMapper.toAppUserDTO(updatedUser);
    }

    @Transactional
    public void deleteUser(UUID id) {
        logger.info("Deleting user with ID: {}", id);
        if (!appUserRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        appUserRepository.deleteById(id);
        logger.info("User deleted with ID: {}", id);
    }

    @Transactional
    public void resetPassword(UUID id, String rawPassword) {
        logger.info("Resetting password for user with ID: {}", id);
        validatePasswordStrength(rawPassword);

        AppUser user = findUserById(id);
        user.setPassword(encodePassword(rawPassword));
        appUserRepository.save(user);

        logger.info("Password reset for user with ID: {}", user.getId());
    }

    public AppUserDTO getUserById(UUID id) {
        logger.info("Retrieving user with ID: {}", id);
        AppUser user = findUserById(id);
        return appUserMapper.toAppUserDTO(user);
    }

    public List<AppUserDTO> getAllUsers() {
        logger.info("Retrieving all users");
        return appUserRepository.findAll().stream()
                .map(appUserMapper::toAppUserDTO)
                .collect(Collectors.toList());
    }

    private String encodePassword(String rawPassword) {
        logger.info("Encoding password");
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Raw password cannot be null or empty");
        }
        return passwordEncoder.encode(rawPassword);
    }

    private void validatePasswordStrength(String rawPassword) {
        logger.info("Validating password strength");
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (!PASSWORD_PATTERN.matcher(rawPassword).matches()) {
            throw new IllegalArgumentException("Password does not meet strength requirements");
        }
    }

    private AppUser findUserById(UUID id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    private Team findTeamById(UUID teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + teamId));
    }

    private void validateUserDTO(AppUserDTO userDTO) {
        if (userDTO == null) {
            throw new IllegalArgumentException("UserDTO cannot be null");
        }
        if (userDTO.getUsername() == null || userDTO.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (userDTO.getTeamId() == null) {
            throw new IllegalArgumentException("Team ID cannot be null");
        }
    }
}