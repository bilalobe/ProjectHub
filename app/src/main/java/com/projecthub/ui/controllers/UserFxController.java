package com.projecthub.ui.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.projecthub.dto.UserSummary;
import com.projecthub.model.User;
import com.projecthub.service.UserService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

@Component
public class UserFXController {

    @Autowired
    private UserService userService;

    @FXML
    private ListView<String> userListView;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button saveUserButton;

    @FXML
    private Button deleteUserButton;

    @FXML
    private Button updateUserButton;

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
    }

    private void loadUsers() {
        List<User> users = userService.getAllUsers();
        ObservableList<String> items = FXCollections.observableArrayList();
        for (User user : users) {
            items.add(user.getId() + ": " + user.getUsername());
        }
        userListView.setItems(items);
    }

    @FXML
    private void handleSaveUser() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            // Handle error: show alert or message to the user
            return;
        }

        UserSummary UserSummary = new UserSummary(null, username, password, null); // Assuming no teamId for now
        User user = new User();
        user.setId(UserSummary.getId());
        user.setUsername(UserSummary.getUsername());
        user.setPassword(UserSummary.getPassword());
        userService.saveUser(user);
        loadUsers();
    }

    @FXML
    private void handleDeleteUser() {
        String selectedItem = userListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            // Handle error: show alert or message to the user
            return;
        }

        Long userId = Long.valueOf(selectedItem.split(":")[0]);
        userService.deleteUser(userId);
        loadUsers();
    }

    @FXML
    private void handleUpdateUser() {
        String selectedItem = userListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            // Handle error: show alert or message to the user
            return;
        }

        Long userId = Long.valueOf(selectedItem.split(":")[0]);
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            // Handle error: show alert or message to the user
            return;
        }

        UserSummary UserSummary = new UserSummary(userId, username, password, null); // Assuming no teamId for now
        User user = new User();
        user.setId(UserSummary.getId());
        user.setUsername(UserSummary.getUsername());
        user.setPassword(UserSummary.getPassword());
        userService.saveUser(user);
        loadUsers();
    }

    // Additional UI handling methods can be added here
}