package com.projecthub.ui.controllers.details;


import com.projecthub.core.exceptions.InvalidInputException;
import com.projecthub.core.exceptions.ResourceNotFoundException;
import com.projecthub.core.exceptions.UserAlreadyExistsException;
import com.projecthub.ui.controllers.BaseController;
import com.projecthub.ui.viewmodels.details.AppUserDetailsViewModel;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller for managing application user details.
 */
@Component
public class AppUserDetailsController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(AppUserDetailsController.class);
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

    /**
     * Constructor with dependencies injected.
     *
     * @param userService      the AppUserService instance
     * @param appUserViewModel the AppUserDetailsViewModel instance
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
                        .collect(Collectors.toList())));
    }

    private void loadUsers() {
        appUserViewModel.loadUsers();
        userListView.setItems(FXCollections.observableArrayList(
                appUserViewModel.getUsers().stream()
                        .map(user -> user.getId() + ": " + user.getUsername())
                        .collect(Collectors.toList())));
    }

    @FXML
    private void handleSaveUser(ActionEvent actionEvent) {
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
                    AppUserDTO userSummary = new AppUserDTO(null, username, password, null, null);
                    userService.createUser(userSummary, password);
                } catch (InvalidInputException | UserAlreadyExistsException e) {
                    logger.error("Error saving user: {}", e.getMessage());
                    throw e;
                } catch (Exception e) {
                    logger.error("Failed to save user", e);
                    throw e;
                }
                return null;
            }
        };
        saveUserTask.setOnSucceeded(this::handleSaveUserSuccess);
        saveUserTask.setOnFailed(this::handleSaveUserFailure);
        new Thread(saveUserTask).start();
    }

    private void handleSaveUserSuccess(WorkerStateEvent event) {
        showAlert("Success", "User saved successfully.");
        loadUsers();
        clearFields();
    }

    private void handleSaveUserFailure(WorkerStateEvent event) {
        Throwable exception = event.getSource().getException();
        if (exception instanceof InvalidInputException) {
            showAlert("Invalid Input", exception.getMessage());
        } else if (exception instanceof UserAlreadyExistsException) {
            showAlert("User Already Exists", exception.getMessage());
        } else {
            showAlert("Error", "Failed to save user: " + exception.getMessage());
        }
    }

    @FXML
    private void handleDeleteUser(ActionEvent actionEvent) {
        String userId = getSelectedUserId();
        if (userId == null) return;

        Task<Void> deleteUserTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    userService.deleteUser(UUID.fromString(userId));
                } catch (ResourceNotFoundException e) {
                    logger.error("Error deleting user: {}", e.getMessage());
                    throw e;
                } catch (Exception e) {
                    logger.error("Failed to delete user", e);
                    throw e;
                }
                return null;
            }
        };
        deleteUserTask.setOnSucceeded(this::handleDeleteUserSuccess);
        deleteUserTask.setOnFailed(this::handleDeleteUserFailure);
        new Thread(deleteUserTask).start();
    }

    private void handleDeleteUserSuccess(WorkerStateEvent event) {
        showAlert("Success", "User deleted successfully.");
        loadUsers();
    }

    private void handleDeleteUserFailure(WorkerStateEvent event) {
        Throwable exception = ((Task<?>) event.getSource()).getException();
        showAlert("Error", "Failed to delete user: " + exception.getMessage());
    }

    @FXML
    private void handleUpdateUser(ActionEvent actionEvent) {
        String userId = getSelectedUserId();
        if (userId == null) return;

        String newPassword = newPasswordField.getText();
        if (newPassword.isEmpty()) {
            showAlert("Error", "New password cannot be empty.");
            return;
        }

        Task<Void> updateUserTask = createUpdateUserTask(userId, newPassword);
        updateUserTask.setOnSucceeded(this::handleUpdateUserSuccess);
        updateUserTask.setOnFailed(this::handleUpdateUserFailure);
        new Thread(updateUserTask).start();
    }

    private Task<Void> createUpdateUserTask(String userId, String newPassword) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    userService.updateUserPassword(UUID.fromString(userId), newPassword);
                } catch (ResourceNotFoundException e) {
                    logger.error("Error updating user: {}", e.getMessage());
                    throw e;
                } catch (Exception e) {
                    logger.error("Failed to update user", e);
                    throw e;
                }
                return null;
            }
        };
    }

    private void handleUpdateUserSuccess(WorkerStateEvent event) {
        showAlert("Success", "User updated successfully.");
        loadUsers();
        clearFields();
    }

    private void handleUpdateUserFailure(WorkerStateEvent event) {
        Throwable exception = ((Task<?>) event.getSource()).getException();
        if (exception instanceof InvalidInputException) {
            showAlert("Invalid Input", exception.getMessage());
        } else if (exception instanceof ResourceNotFoundException) {
            showAlert("Resource Not Found", exception.getMessage());
        } else {
            showAlert("Error", "Failed to update user: " + exception.getMessage());
        }
    }

    @FXML
    private void handleResetPassword(ActionEvent actionEvent) {
        String userId = getSelectedUserId();
        if (userId == null) return;

        String newPassword = newPasswordField.getText();
        if (newPassword.isEmpty()) {
            showAlert("Error", "New password cannot be empty.");
            return;
        }

        Task<Void> resetPasswordTask = createResetPasswordTask(userId, newPassword);
        resetPasswordTask.setOnSucceeded(this::handleResetPasswordSuccess);
        resetPasswordTask.setOnFailed(this::handleResetPasswordFailure);
        new Thread(resetPasswordTask).start();
    }

    private Task<Void> createResetPasswordTask(String userId, String newPassword) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    userService.resetPassword(UUID.fromString(userId), newPassword);
                } catch (ResourceNotFoundException e) {
                    logger.error("Error resetting password: {}", e.getMessage());
                    throw e;
                } catch (Exception e) {
                    logger.error("Failed to reset password", e);
                    throw e;
                }
                return null;
            }
        };
    }

    private void handleResetPasswordSuccess(WorkerStateEvent event) {
        showAlert("Success", "Password reset successfully.");
        loadUsers();
        clearFields();
    }

    private void handleResetPasswordFailure(WorkerStateEvent event) {
        Throwable exception = ((Task<?>) event.getSource()).getException();
        if (exception instanceof InvalidInputException) {
            showAlert("Invalid Input", exception.getMessage());
        } else if (exception instanceof ResourceNotFoundException) {
            showAlert("Resource Not Found", exception.getMessage());
        } else {
            showAlert("Error", "Failed to reset password: " + exception.getMessage());
        }
    }

    private String getSelectedUserId() {
        String selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Error", "No user selected.");
            return null;
        }
        return selectedUser.split(":")[0].trim();
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