package com.projecthub.ui.viewmodels.details;

import com.projecthub.core.dto.AppUserDTO;
import com.projecthub.core.dto.RegisterRequestDTO;
import com.projecthub.core.dto.UpdateUserRequestDTO;
import com.projecthub.core.services.AppUserService;
import jakarta.annotation.PostConstruct;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ViewModel for managing application user details.
 */
@Component
public class AppUserDetailsViewModel {

    private static final Logger logger = LoggerFactory.getLogger(AppUserDetailsViewModel.class);

    private final AppUserService userService;

    /**
     * -- GETTER --
     * Gets the list of users.
     *
     * @return the observable list of users
     */
    @Getter
    private final ObservableList<AppUserDTO> users = FXCollections.observableArrayList();

    /**
     * Constructor with dependency injection.
     *
     * @param userService the user service
     */
    public AppUserDetailsViewModel(AppUserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        loadUsers();
    }

    /**
     * Loads users from the service.
     */
    public void loadUsers() {
        users.clear();
        try {
            List<AppUserDTO> userList = userService.getAllUsers();
            users.addAll(userList);
        } catch (Exception e) {
            logger.error("Failed to load users", e);
        }
    }

    /**
     * Adds a new user.
     *
     * @param user the user DTO
     */
    public void addUser(AppUserDTO user) {
        if (user == null) {
            logger.warn("User is null in addUser()");
            return;
        }
        try {
            RegisterRequestDTO registerRequest = new RegisterRequestDTO();
            userService.createUser(registerRequest);
            users.add(user);
        } catch (Exception e) {
            logger.error("Failed to add user: {}", user, e);
        }
    }

    /**
     * Updates an existing user.
     *
     * @param user the user DTO
     */
    public void updateUser(AppUserDTO user) {
        if (user == null) {
            logger.warn("User is null in updateUser()");
            return;
        }
        try {
            UpdateUserRequestDTO updateUserRequest = new UpdateUserRequestDTO();
            userService.updateUser(user.getId(), updateUserRequest);
            int index = users.indexOf(user);
            if (index >= 0) {
                users.set(index, user);
            }
        } catch (Exception e) {
            logger.error("Failed to update user: {}", user, e);
        }
    }

    /**
     * Deletes a user.
     *
     * @param user the user DTO
     */
    public void deleteUser(AppUserDTO user) {
        if (user == null) {
            logger.warn("User is null in deleteUser()");
            return;
        }
        try {
            userService.deleteUser(user.getId());
            users.remove(user);
        } catch (Exception e) {
            logger.error("Failed to delete user: {}", user, e);
        }
    }
}