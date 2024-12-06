package com.projecthub.ui.controllers.details;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.projecthub.dto.AppUserSummary;
import com.projecthub.exception.InvalidInputException;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.exception.UserAlreadyExistsException;
import com.projecthub.service.UserService;
import com.projecthub.ui.viewmodels.UserViewModel;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

@Component
public class UserDetailsController {

    
    private UserService userService;

    
    private UserViewModel userViewModel;

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

    // Initialize method called by FXMLLoader
    @FXML
    public void initialize() {
        initializeUI();
        loadUsers();
    }

    private void initializeUI() {
        saveUserButton.setOnAction(event -> handleSaveUser());
        deleteUserButton.setOnAction(event -> handleDeleteUser());
        updateUserButton.setOnAction(event -> handleUpdateUser());
        resetPasswordButton.setOnAction(event -> handleResetPassword());

        userListView.setItems(FXCollections.observableArrayList(
            userViewModel.getUsers().stream()
                .map(user -> user.getId() + ": " + user.getUsername())
                .collect(Collectors.toList())
        ));
    }

    private void loadUsers() {
        userViewModel.loadUsers();
        userListView.setItems(FXCollections.observableArrayList(
            userViewModel.getUsers().stream()
                .map(user -> user.getId() + ": " + user.getUsername())
                .collect(Collectors.toList())
        ));
    }

    @FXML
    private void handleSaveUser() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username and password cannot be empty.");
            return;
        }

        try {
            AppUserSummary userSummary = new AppUserSummary(null, username, password, null, null, null); // Assuming no teamId for now
            userService.saveUser(userSummary, password);
            loadUsers();
            clearFields();
        } catch (InvalidInputException e) {
            showAlert("Invalid Input", e.getMessage());
        } catch (ResourceNotFoundException e) {
            showAlert("Resource Not Found", e.getMessage());
        } catch (UserAlreadyExistsException e) {
            showAlert("User Already Exists", e.getMessage());
        } catch (Exception e) {
            showAlert("Error", "Failed to save user: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteUser() {
        String selectedItem = userListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("Error", "No user selected.");
            return;
        }

        try {
            Long userId = Long.valueOf(selectedItem.split(":")[0]);
            userService.deleteUser(userId);
            loadUsers();
            clearFields();
        } catch (ResourceNotFoundException e) {
            showAlert("Resource Not Found", e.getMessage());
        } catch (NumberFormatException e) {
            showAlert("Error", "Failed to delete user: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateUser() {
        String selectedItem = userListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("Error", "No user selected.");
            return;
        }

        Long userId = Long.valueOf(selectedItem.split(":")[0]);
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username and password cannot be empty.");
            return;
        }

        try {
            AppUserSummary userSummary;
            userSummary = new AppUserSummary(userId, username, password, null, null, null); // Assuming no teamId for now
            userService.saveUser(userSummary, password);
            loadUsers();
            clearFields();
        } catch (InvalidInputException e) {
            showAlert("Invalid Input", e.getMessage());
        } catch (ResourceNotFoundException e) {
            showAlert("Resource Not Found", e.getMessage());
        } catch (UserAlreadyExistsException e) {
            showAlert("User Already Exists", e.getMessage());
        } catch (Exception e) {
            showAlert("Error", "Failed to update user: " + e.getMessage());
        }
    }

    @FXML
    private void handleResetPassword() {
        String selectedItem = userListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("Error", "No user selected.");
            return;
        }

        Long userId = Long.valueOf(selectedItem.split(":")[0]);
        String newPassword = newPasswordField.getText();

        if (newPassword.isEmpty()) {
            showAlert("Error", "New password cannot be empty.");
            return;
        }

        try {
            userService.resetPassword(userId, newPassword);
            showAlert("Success", "Password reset successfully.");
            clearFields();
        } catch (ResourceNotFoundException e) {
            showAlert("Resource Not Found", e.getMessage());
        } catch (InvalidInputException e) {
            showAlert("Invalid Input", e.getMessage());
        } catch (Exception e) {
            showAlert("Error", "Failed to reset password: " + e.getMessage());
        }
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