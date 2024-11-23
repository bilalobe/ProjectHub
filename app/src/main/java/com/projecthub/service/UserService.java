package com.projecthub.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projecthub.dto.AppUserSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.exception.UserAlreadyExistsException;
import com.projecthub.mapper.AppUserMapper;
import com.projecthub.model.Team;
import com.projecthub.model.AppUser;
import com.projecthub.repository.custom.CustomUserRepository;
import com.projecthub.repository.custom.CustomTeamRepository;

/**
 * Service class for managing users within the ProjectHub application.
 * It provides methods to create, retrieve, update, and delete users.
 */
@Service
public class UserService {

    private final CustomUserRepository userRepository;
    private final CustomTeamRepository teamRepository;
    private final PasswordService passwordService;

    @Autowired
    public UserService(CustomUserRepository userRepository, CustomTeamRepository teamRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.passwordService = passwordService;
    }

    /**
     * Saves a new user based on the provided {@link AppUserSummary} DTO.
     * 
     * @param userSummary the summary of the user to be created
     * @param password the password of the user
     * @return the saved {@link AppUserSummary} entity
     * @throws UserAlreadyExistsException if the username is already taken
     */
    public AppUserSummary saveUser(AppUserSummary userSummary, String password) throws UserAlreadyExistsException {
        if (userRepository.findByUsername(userSummary.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists with username: " + userSummary.getUsername());
        }

        Team team = null;
        if (userSummary.getTeam() != null) {
            team = teamRepository.findById(userSummary.getTeam())
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + userSummary.getTeam()));
        }

        AppUser user = AppUserMapper.toAppUser(userSummary, team, passwordService, password);
        AppUser savedUser = userRepository.save(user);
        return AppUserMapper.toAppUserSummary(savedUser);
    }

    /**
     * Retrieves all users from the repository.
     * 
     * @return a list of all {@link AppUserSummary} objects
     */
    public List<AppUserSummary> getAllUsers() {
        return userRepository.findAll().stream()
                .map(AppUserMapper::toAppUserSummary)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a user by its unique identifier.
     * 
     * @param id the unique identifier of the user
     * @return an {@link Optional} containing the {@link AppUserSummary} if found, else empty
     */
    public Optional<AppUserSummary> getUserById(Long id) {
        return userRepository.findById(id)
                .map(AppUserMapper::toAppUserSummary);
    }

    /**
     * Retrieves an AppUser entity by its unique identifier.
     * 
     * @param id the unique identifier of the user
     * @return an {@link Optional} containing the {@link AppUser} if found, else empty
     */
    public Optional<AppUser> getAppUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Deletes a user based on its unique identifier.
     * 
     * @param id the unique identifier of the user to be deleted
     * @throws ResourceNotFoundException if the user is not found
     */
    public void deleteUser(Long id) throws ResourceNotFoundException {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Updates an existing user based on the provided {@link AppUserSummary} DTO.
     * 
     * @param userSummary the summary containing updated user information
     * @param password the password of the user
     * @return the updated {@link AppUserSummary} entity
     * @throws ResourceNotFoundException if the user or team is not found
     */
    public AppUserSummary updateUser(AppUserSummary userSummary, String password) throws ResourceNotFoundException {
        AppUser user = userRepository.findById(userSummary.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userSummary.getId()));

        Team team = null;
        if (userSummary.getTeam() != null) {
            team = teamRepository.findById(userSummary.getTeam())
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + userSummary.getTeam()));
        }

        user.setUsername(userSummary.getUsername());
        if (password != null && !password.isEmpty()) {
            String encodedPassword = passwordService.encodePassword(password);
            user.setPassword(encodedPassword);
        }
        user.setTeam(team);
        AppUser savedUser = userRepository.save(user);
        return AppUserMapper.toAppUserSummary(savedUser);
    }
}