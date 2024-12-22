package com.projecthub.ui.controllers;

import com.projecthub.ui.services.locale.LanguageService;
import com.projecthub.ui.services.theme.ThemeService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class SettingsViewController {

    private final LanguageService languageService;
    private final ThemeService themeService;
    @FXML
    private ComboBox<String> themeComboBox;
    @FXML
    private ComboBox<String> languageComboBox;
    @FXML
    private CheckBox emailNotificationsCheckBox;
    @FXML
    private Button changePasswordButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;


    public SettingsViewController(LanguageService languageService, ThemeService themeService) {
        this.languageService = languageService;
        this.themeService = themeService;
    }

    @FXML
    public void initialize() {
        // Initialize Theme ComboBox
        themeComboBox.getItems().addAll("Light", "Dark");
        themeComboBox.setValue(ThemeService.getCurrentTheme());

        // Initialize Language ComboBox
        languageComboBox.getItems().addAll("English", "Spanish", "Arabic");
        languageComboBox.setValue(languageService.getCurrentLocale().getDisplayLanguage());

        // Initialize Notifications
        emailNotificationsCheckBox.setSelected(true); // Example default

        // Set Event Handlers
        saveButton.setOnAction(this::handleSave);
        cancelButton.setOnAction(this::handleCancel);
        changePasswordButton.setOnAction(this::handleChangePassword);
        logoutButton.setOnAction(this::handleLogout);
    }

    private void handleSave(ActionEvent event) {
        // Save Theme
        String selectedTheme = themeComboBox.getValue();
        themeService.setTheme(selectedTheme);

        // Save Language
        String selectedLanguage = languageComboBox.getValue();
        Locale locale = Locale.ENGLISH; // Default
        if ("Spanish".equalsIgnoreCase(selectedLanguage)) {
            locale = new Locale("es");
        } else if ("Arabic".equalsIgnoreCase(selectedLanguage)) {
            locale = new Locale("ar");
        }
        languageService.setLocale(locale);

        // Save Notifications
        boolean emailNotifications = emailNotificationsCheckBox.isSelected();
        // TODO: Implement saving notification preferences

        showAlert(Alert.AlertType.INFORMATION, "Success", "Settings have been saved successfully.");

        // Optionally, navigate back to the previous view
    }

    private void handleCancel(ActionEvent event) {
        // Optionally, navigate back without saving
        // For example, clear selections or reset to previous values

        // Reload current settings if necessary
        themeComboBox.setValue(ThemeService.getCurrentTheme());
        languageComboBox.setValue(languageService.getCurrentLocale().getDisplayLanguage());
        emailNotificationsCheckBox.setSelected(true); // Reset to default or previous state
    }

    private void handleChangePassword(ActionEvent event) {
        // TODO: Implement password change dialog
        showAlert(Alert.AlertType.INFORMATION, "Change Password", "Password change functionality is not yet implemented.");
    }

    private void handleLogout(ActionEvent event) {
        // TODO: Implement logout functionality
        showAlert(Alert.AlertType.INFORMATION, "Logout", "Logout functionality is not yet implemented.");
    }

    /**
     * Displays an alert dialog with the specified type, title, and content.
     *
     * @param alertType the type of the alert
     * @param title     the title of the alert
     * @param content   the content message
     */
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(localize(title));
        alert.setHeaderText(null);
        alert.setContentText(localize(content));
        alert.showAndWait();
    }

    /**
     * Localizes a given string key using the current ResourceBundle.
     *
     * @param key the key to localize
     * @return the localized string
     */
    private String localize(String key) {
        try {
            return languageService.getResourceBundle().getString(key);
        } catch (Exception e) {
            // Fallback to key if not found
            return key;
        }
    }
}