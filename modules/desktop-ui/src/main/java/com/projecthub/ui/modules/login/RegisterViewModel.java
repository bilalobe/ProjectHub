package com.projecthub.ui.modules.login;

import com.projecthub.base.auth.api.dto.RegisterRequestDTO;
import com.projecthub.base.shared.validators.PasswordValidator;
import com.projecthub.ui.shared.utils.ValidationUtils;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RegisterViewModel {
    private static final Logger logger = LoggerFactory.getLogger(RegisterViewModel.class);

    private final PasswordValidator passwordValidator;
    private final AppUserService appUserService;

    private final StringProperty username = new SimpleStringProperty("");
    private final StringProperty email = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");
    private final StringProperty confirmPassword = new SimpleStringProperty("");
    private final StringProperty firstName = new SimpleStringProperty("");
    private final StringProperty lastName = new SimpleStringProperty("");
    private final ObjectProperty<String> selectedRole = new SimpleObjectProperty<>();
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final BooleanProperty registrationInProgress = new SimpleBooleanProperty(false);
    private final ObservableList<String> availableRoles = FXCollections.observableArrayList();


    public RegisterViewModel(PasswordValidator passwordValidator, AppUserService appUserService) {
        this.passwordValidator = passwordValidator;
        this.appUserService = appUserService;
        initializeRoles();
    }

    private void initializeRoles() {
        availableRoles.addAll("Student", "Teacher", "Admin");
    }

    // Properties
    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public StringProperty confirmPasswordProperty() {
        return confirmPassword;
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public ObjectProperty<String> selectedRoleProperty() {
        return selectedRole;
    }

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    public BooleanProperty registrationInProgressProperty() {
        return registrationInProgress;
    }

    public ObservableList<String> getAvailableRoles() {
        return availableRoles;
    }

    public boolean register() {
        try {
            registrationInProgress.set(true);
            errorMessage.set("");

            if (!validateInput()) {
                return false;
            }

            RegisterRequestDTO registration = new RegisterRequestDTO();
            registration.setUsername(username.get());
            registration.setEmail(email.get());
            registration.setPassword(password.get());
            registration.setFirstName(firstName.get());
            registration.setLastName(lastName.get());

            appUserService.registerUser(registration);
            return true;

        } catch (Exception e) {
            logger.error("Registration failed", e);
            errorMessage.set("Registration failed: " + e.getMessage());
            return false;
        } finally {
            registrationInProgress.set(false);
        }
    }

    private boolean validateInput() {
        if (username.get().isEmpty()) {
            errorMessage.set("Username is required");
            return false;
        }

        if (email.get().isEmpty() || !email.get().contains("@")) {
            errorMessage.set("Valid email is required");
            return false;
        }

        if (passwordValidator.isPasswordStrong(password.get())) {
            errorMessage.set("Password does not meet requirements");
            return false;
        }

        if (!password.get().equals(confirmPassword.get())) {
            errorMessage.set("Passwords do not match");
            return false;
        }

        if (firstName.get().isEmpty()) {
            errorMessage.set("First name is required");
            return false;
        }

        if (lastName.get().isEmpty()) {
            errorMessage.set("Last name is required");
            return false;
        }

        if (selectedRole.get() == null) {
            errorMessage.set("Role selection is required");
            return false;
        }

        return true;
    }

    public boolean validateForm(String grade, String feedback) {
        return ValidationUtils.isValidGrade(grade) && ValidationUtils.isNotEmpty(feedback);
    }

    public void clearFields() {
        username.set("");
        email.set("");
        password.set("");
        confirmPassword.set("");
        firstName.set("");
        lastName.set("");
        selectedRole.set(null);
        errorMessage.set("");
    }
}
