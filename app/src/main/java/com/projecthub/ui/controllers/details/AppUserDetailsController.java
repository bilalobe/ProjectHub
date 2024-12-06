package com.projecthub.ui.controllers.details;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.projecthub.dto.AppUserDTO;
import com.projecthub.exception.InvalidInputException;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.exception.UserAlreadyExistsException;
import com.projecthub.ui.controllers.BaseController;
import com.projecthub.ui.viewmodels.details.AppUserDetailsViewModel;
import com.projecthub.service.AppUserService;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for managing application user details.
 */
@Component
public class AppUserDetailsController extends BaseController {

    private final AppUserService userService;
    private final AppUserDetailsViewModel appUserViewModel;

    @FXML
    private ListView<String> userListView;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField newPasswordField;

    @FXML
    private Button saveUserButton;

    @FXML
    private Button deleteUserButton;

    @FXML
    private Button updateUserButton;

    @FXML
    private Button resetPasswordButton;

    private static final Logger logger = LoggerFactory.getLogger(AppUserDetailsController.class);

    /**
     * Constructor with dependencies injected.
     *
     * @param userService        the AppUserService instance
     * @param appUserViewModel   the AppUserDetailsViewModel instance
     */
    public AppUserDetailsController(AppUserService userService, AppUserDetailsViewModel appUserViewModel) {
        this.userService = userService;
        this.appUserViewModel = appUserViewModel;
    }

    @FXML
    public void initialize() {
        initializeUI();
        loadUsers();
    }

    private void initializeUI() {
        saveUserButton.setOnAction(this::handleSaveUser);
        deleteUserButton.setOnAction(this::handleDeleteUser);
        updateUserButton.setOnAction(this::handleUpdateUser);
        resetPasswordButton.setOnAction(this::handleResetPassword);
    
        userListView.setItems(FXCollections.observableArrayList(
            appUserViewModel.getUsers().stream()
                .map(user -> user.getId() + ": " + user.getUsername())
                .collect(Collectors.toList())
        ));
    }

    private void loadUsers() {
        appUserViewModel.loadUsers();
        userListView.setItems(FXCollections.observableArrayList(
            appUserViewModel.getUsers().stream()
                .map(user -> user.getId() + ": " + user.getUsername())
                .collect(Collectors.toList())
        ));
    }

    @FXML
    private void handleSaveUser(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username and password cannot be empty.");
            return;
        }

        Task<Void> saveUserTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    AppUserDTO userSummary = new AppUserDTO(null, username, password, null, null, null); // Assuming no teamId for now
                    userService.createUser(userSummary, password);
                } catch (InvalidInputException e) {
                    logger.error("Invalid input: {}", e.getMessage());
                    throw e;
                } catch (UserAlreadyExistsException e) {
                    logger.error("User already exists: {}", e.getMessage());
                    throw e;
                } catch (Exception e) {
                    logger.error("Failed to save user", e);
                    throw e;
                }
                return null;
            }
        };
        saveUserTask.setOnSucceeded(_ -> {
            showAlert("Success", "User saved successfully.");
            loadUsers();
            clearFields();
        });
        saveUserTask.setOnFailed(_ -> {
            Throwable exception = saveUserTask.getException();
            if (exception instanceof InvalidInputException) {
                showAlert("Invalid Input", exception.getMessage());
            } else if (exception instanceof UserAlreadyExistsException) {
                showAlert("User Already Exists", exception.getMessage());
            } else {
                showAlert("Error", "Failed to save user: " + exception.getMessage());
            }
        });
        new Thread(saveUserTask).start();
    }

    @FXML
    private void handleDeleteUser(ActionEvent event) {
        String selectedItem = userListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("Error", "No user selected.");
            return;
        }

        try {
            UUID userId = UUID.fromString(selectedItem.split(":")[0]);
            userService.deleteUser(userId);
            loadUsers();
            clearFields();
        } catch (ResourceNotFoundException e) {
            showAlert("Resource Not Found", e.getMessage());
        } catch (NumberFormatException e) {
            showAlert("Error", "Failed to delete user: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to delete user", e);
            showAlert("Error", "Failed to delete user: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateUser(ActionEvent event) {
        String selectedItem = userListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("Error", "No user selected.");
            return;
        }

        UUID userId = UUID.fromString(selectedItem.split(":")[0]);
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username and password cannot be empty.");
            return;
        }

        Task<Void> updateUserTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    AppUserDTO userSummary;
                    userSummary = new AppUserDTO(userId, username, password, null, null, null); // Assuming no teamId for now
                    userService.updateUser(userId, userSummary, password);
                } catch (InvalidInputException e) {
                    logger.error("Invalid input: {}", e.getMessage());
                    throw e;
                } catch (ResourceNotFoundException e) {
                    logger.error("Resource not found: {}", e.getMessage());
                    throw e;
                } catch (Exception e) {
                    logger.error("Failed to update user", e);
                    throw e;
                }
                return null;
            }
        };
        updateUserTask.setOnSucceeded(_ -> {
            showAlert("Success", "User updated successfully.");
            loadUsers();
            clearFields();
        });
        updateUserTask.setOnFailed(_ -> {
            Throwable exception = updateUserTask.getException();
            if (exception instanceof InvalidInputException) {
                showAlert("Invalid Input", exception.getMessage());
            } else if (exception instanceof ResourceNotFoundException) {
                showAlert("Resource Not Found", exception.getMessage());
            } else {
                showAlert("Error", "Failed to update user: " + exception.getMessage());
            }
        });
        new Thread(updateUserTask).start();
    }

    @FXML
    private void handleResetPassword(ActionEvent event) {
        String selectedItem = userListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("Error", "No user selected.");
            return;
        }

        UUID userId = UUID.fromString(selectedItem.split(":")[0]);
        String newPassword = newPasswordField.getText();

        if (newPassword.isEmpty()) {
            showAlert("Error", "New password cannot be empty.");
            return;
        }

        Task<Void> resetPasswordTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    userService.resetPassword(userId, newPassword);
                } catch (InvalidInputException e) {
                    logger.error("Invalid input: {}", e.getMessage());
                    throw e;
                } catch (ResourceNotFoundException e) {
                    logger.error("Resource not found: {}", e.getMessage());
                    throw e;
                } catch (Exception e) {
                    logger.error("Failed to reset password", e);
                    throw e;
                }
                return null;
            }
        };
        resetPasswordTask.setOnSucceeded(_ -> {
            showAlert("Success", "Password reset successfully.");
            clearFields();
        });
        resetPasswordTask.setOnFailed(_ -> {
            Throwable exception = resetPasswordTask.getException();
            if (exception instanceof InvalidInputException) {
                showAlert("Invalid Input", exception.getMessage());
            } else if (exception instanceof ResourceNotFoundException) {
                showAlert("Resource Not Found", exception.getMessage());
            } else {
                showAlert("Error", "Failed to reset password: " + exception.getMessage());
            }
        });
        new Thread(resetPasswordTask).start();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        newPasswordField.clear();
    }
}