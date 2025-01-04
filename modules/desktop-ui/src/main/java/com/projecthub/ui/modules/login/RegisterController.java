package com.projecthub.ui.modules.login;

import com.projecthub.ui.shared.utils.BaseController;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

@Component
public class RegisterController extends BaseController {
    private final RegisterViewModel registerViewModel;
    private final BooleanProperty rtl = new SimpleBooleanProperty(false);
    @FXML
    private ResourceBundle resources;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private Label errorLabel;
    @FXML
    private Button registerButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Hyperlink loginLink;
    @FXML
    private TextField passwordTextField;
    @FXML
    private TextField confirmPasswordTextField;
    @FXML
    private ToggleButton passwordToggle;
    @FXML
    private ToggleButton confirmPasswordToggle;

    public RegisterController(RegisterViewModel registerViewModel) {
        this.registerViewModel = registerViewModel;
    }

    @FXML
    public void initialize() {
        // Check if current locale is RTL
        rtl.set(resources.getString("direction").equals("rtl"));

        if (rtl.get()) {
            cohortDetailsView.getStyleClass().add("rtl");
        }
        bindProperties();
        setupEventHandlers();
        setupValidation();
    }

    public BooleanProperty rtlProperty() {
        return rtl;
    }

    public boolean isRTL() {
        return rtl.get();
    }

    private void bindProperties() {
        usernameField.textProperty().bindBidirectional(registerViewModel.usernameProperty());
        emailField.textProperty().bindBidirectional(registerViewModel.emailProperty());
        passwordField.textProperty().bindBidirectional(registerViewModel.passwordProperty());
        confirmPasswordField.textProperty().bindBidirectional(registerViewModel.confirmPasswordProperty());

        roleComboBox.setItems(registerViewModel.getAvailableRoles());
        roleComboBox.valueProperty().bindBidirectional(registerViewModel.selectedRoleProperty());

        errorLabel.textProperty().bind(registerViewModel.errorMessageProperty());
        errorLabel.visibleProperty().bind(registerViewModel.errorMessageProperty().isNotEmpty());

        registerButton.disableProperty().bind(registerViewModel.registrationInProgressProperty());
    }

    private void setupEventHandlers() {
        registerButton.setOnAction(event -> handleRegister());
        cancelButton.setOnAction(event -> handleCancel());
        loginLink.setOnAction(event -> navigateToLogin());

        // Setup password visibility toggles
        setupPasswordToggle(passwordField, passwordTextField, passwordToggle);
        setupPasswordToggle(confirmPasswordField, confirmPasswordTextField, confirmPasswordToggle);
    }

    private void setupPasswordToggle(PasswordField passwordField, TextField textField, ToggleButton toggleButton) {
        // Set up Material Design icons
        toggleButton.setGraphic(MaterialDesignIcon.VISIBILITY_OFF.graphic());
        toggleButton.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            toggleButton.setGraphic(isSelected ?
                MaterialDesignIcon.VISIBILITY.graphic() :
                MaterialDesignIcon.VISIBILITY_OFF.graphic());
        });

        // Bind visibility
        textField.managedProperty().bind(toggleButton.selectedProperty());
        textField.visibleProperty().bind(toggleButton.selectedProperty());
        passwordField.managedProperty().bind(toggleButton.selectedProperty().not());
        passwordField.visibleProperty().bind(toggleButton.selectedProperty().not());

        // Bind values
        textField.textProperty().bindBidirectional(passwordField.textProperty());
    }

    private void setupValidation() {
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean isValid = !newVal.trim().isEmpty() && newVal.length() >= 3;
            updateFieldValidation(usernameField, isValid,
                isValid ? getMessage("validation.username.valid") : getMessage("validation.username.invalid"));
        });

        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean isValid = newVal.contains("@") && newVal.length() > 3;
            updateFieldValidation(emailField, isValid,
                isValid ? getMessage("validation.email.valid") : getMessage("validation.email.invalid"));
        });

        // Add placeholder text that disappears on focus
        emailField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused && emailField.getText().isEmpty()) {
                emailField.setPromptText(getMessage("register.email.prompt"));
            }
        });

        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            PasswordStrength strength = getPasswordStrength(newVal);
            updatePasswordStrength(strength);
        });

        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean isValid = newVal.equals(passwordField.getText());
            updateFieldValidation(confirmPasswordField, isValid,
                isValid ? getMessage("validation.password.match") : getMessage("validation.password.mismatch"));
        });
    }

    private String getMessage(String key) {
        return getResourceBundle().getString(key);
    }

    private void validateField(Control field, boolean isValid) {
        field.pseudoClassStateChanged(getPseudoClass("error"), !isValid);
    }

    private void updateFieldValidation(Control field, boolean isValid, String message) {
        field.getStyleClass().removeAll("field-valid", "field-invalid");
        field.getStyleClass().add(isValid ? "field-valid" : "field-invalid");

        Tooltip tooltip = new Tooltip(message);
        tooltip.getStyleClass().add("validation-tooltip");
        field.setTooltip(tooltip);
        tooltip.setNodeOrientation(rtl.get() ?
            NodeOrientation.RIGHT_TO_LEFT :
            NodeOrientation.LEFT_TO_RIGHT);
    }

    private void updatePasswordStrength(PasswordStrength strength) {
        ProgressBar strengthBar = new ProgressBar();
        strengthBar.getStyleClass().add("strength-indicator");

        switch (strength) {
            case WEAK:
                strengthBar.setProgress(0.33);
                strengthBar.getStyleClass().add("strength-weak");
                break;
            case MEDIUM:
                strengthBar.setProgress(0.66);
                strengthBar.getStyleClass().add("strength-medium");
                break;
            case STRONG:
                strengthBar.setProgress(1.0);
                strengthBar.getStyleClass().add("strength-strong");
                break;
        }

        // Add strength bar below password field
        // Assuming password field is in a GridPane
        GridPane parent = (GridPane) passwordField.getParent();
        parent.add(strengthBar, 1, GridPane.getRowIndex(passwordField) + 1);
    }

    private PasswordStrength getPasswordStrength(String password) {
        int score = 0;

        if (password.length() >= 8) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[!@#$%^&*].*")) score++;

        if (score <= 2) return PasswordStrength.WEAK;
        if (score <= 4) return PasswordStrength.MEDIUM;
        return PasswordStrength.STRONG;
    }

    private void handleRegister() {
        runAsync(() -> {
            boolean success = registerViewModel.register();
            if (success) {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.INFORMATION,
                        "Registration Successful",
                        "Your account has been created successfully.");
                    navigateToLogin();
                });
            }
        });
    }

    private void handleCancel() {
        registerViewModel.clearFields();
        navigateToLogin();
    }

    private void navigateToLogin() {
        navigate("/com/projecthub/ui/views/LoginView.fxml");
    }

    private enum PasswordStrength {
        WEAK, MEDIUM, STRONG
    }
}
