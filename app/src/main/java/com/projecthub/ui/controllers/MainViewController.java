package com.projecthub.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

@Component
public class MainViewController {

    @FXML
    private StackPane mainContent;

    @FXML
    private VBox navigationPane;

    @FXML
    private VBox contextualSidebar;

    private ContextMenu contextMenu;

    public void initialize() {
        // Initialization logic here
        contextMenu = new ContextMenu();
        MenuItem refreshItem = new MenuItem("Refresh");
        refreshItem.setOnAction(this::handleRefresh);
        MenuItem settingsItem = new MenuItem("Settings");
        settingsItem.setOnAction(this::handleSettings);
        contextMenu.getItems().addAll(refreshItem, settingsItem);
        mainContent.setOnContextMenuRequested(event -> contextMenu.show(mainContent, event.getScreenX(), event.getScreenY()));
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    @FXML
    private void handlePreferences() {
        // Handle preferences action
        showAlert("Preferences", "Open preferences dialog.");
    }

    @FXML
    private void showDashboard() {
        // Load and display the dashboard content in mainContent
        showAlert("Dashboard", "Show dashboard content.");
    }

    @FXML
    private void showProjects() {
        // Load and display the projects content in mainContent
        showAlert("Projects", "Show projects content.");
    }

    @FXML
    private void showTeams() {
        // Load and display the teams content in mainContent
        showAlert("Teams", "Show teams content.");
    }

    @FXML
    private void showUsers() {
        // Load and display the users content in mainContent
        showAlert("Users", "Show users content.");
    }

    @FXML
    private void handleAbout() {
        // Handle about action
        TabPane tabPane = new TabPane();

        Tab tab1 = new Tab("Overview");
        tab1.setContent(new Label("This is the overview tab."));

        Tab tab2 = new Tab("Details");
        tab2.setContent(new Label("This is the details tab."));

        tabPane.getTabs().addAll(tab1, tab2);

        mainContent.getChildren().clear();
        mainContent.getChildren().add(tabPane);
    }

    @FXML
    public void handleRefresh(javafx.event.ActionEvent event) {
        // Handle refresh action
        showAlert("Refresh", "Refresh the content.");
    }

    @FXML
    public void handleSettings(javafx.event.ActionEvent event) {
        // Handle settings action
        showAlert("Settings", "Open settings dialog.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}