package com.projecthub.ui.viewmodels;

import com.projecthub.core.auth.service.AuthenticationService;
import com.projecthub.core.dto.AuthenticationResult;
import com.projecthub.core.dto.LoginRequestDTO;
import com.projecthub.core.exceptions.AccountDisabledException;
import com.projecthub.core.exceptions.AccountLockedException;
import com.projecthub.core.exceptions.InvalidCredentialsException;
import com.projecthub.core.services.auth.RememberMeService;
import com.projecthub.core.services.auth.TokenManagementService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoginViewModel {
    private static final Logger logger = LoggerFactory.getLogger(LoginViewModel.class);

    private final AuthenticationService authService;
    private final TokenManagementService tokenService;
    private final RememberMeService rememberMeService;

    private final StringProperty username = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");
    private final BooleanProperty rememberMe = new SimpleBooleanProperty(false);
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final BooleanProperty loginInProgress = new SimpleBooleanProperty(false);
    private final BooleanProperty passkeyAvailable = new SimpleBooleanProperty(false);
    private final StringProperty githubAuthUrl = new SimpleStringProperty();

    public LoginViewModel(
            AuthenticationService authService,
            TokenManagementService tokenService,
            RememberMeService rememberMeService) {
        this.authService = authService;
        this.tokenService = tokenService;
        this.rememberMeService = rememberMeService;
        checkPasskeyAvailability();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public BooleanProperty rememberMeProperty() {
        return rememberMe;
    }

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    public BooleanProperty loginInProgressProperty() {
        return loginInProgress;
    }

    public BooleanProperty passkeyAvailableProperty() {
        return passkeyAvailable;
    }

    public StringProperty githubAuthUrlProperty() {
        return githubAuthUrl;
    }

    public boolean login() {
        try {
            loginInProgress.set(true);
            errorMessage.set("");

            if (!validateInput()) {
                return false;
            }

            LoginRequestDTO loginRequest = new LoginRequestDTO();
            loginRequest.setUsername(username.get());
            loginRequest.setPassword(password.get());
            loginRequest.setRememberMe(rememberMe.get());
            // Add IP address if needed for audit
            loginRequest.setIpAddress(getClientIpAddress());

            AuthenticationResult result = authService.authenticate(loginRequest);

            if (result.getToken() != null) {
                // Store the access token securely
                tokenService.storeToken(result.getToken());

                // Handle remember-me if enabled
                if (rememberMe.get() && result.getRememberMeToken() != null) {
                    rememberMeService.storeToken(result.getRememberMeToken());
                }

                return true;
            }

            errorMessage.set("Authentication failed");
            return false;

        } catch (Exception e) {
            logger.error("Login failed", e);
            errorMessage.set(getErrorMessage(e));
            return false;
        } finally {
            loginInProgress.set(false);
        }
    }

    private String getErrorMessage(Exception e) {
        if (e instanceof AccountLockedException) {
            return getMessage("login.error.account.locked");
        } else if (e instanceof AccountDisabledException) {
            return getMessage("login.error.account.disabled");
        } else if (e instanceof InvalidCredentialsException) {
            return getMessage("login.error.invalid.credentials");
        }
        return getMessage("login.error.general");
    }

    private boolean validateInput() {
        if (username.get().isEmpty()) {
            errorMessage.set(getMessage("login.error.username.required"));
            return false;
        }
        if (password.get().isEmpty()) {
            errorMessage.set(getMessage("login.error.password.required"));
            return false;
        }
        return true;
    }

    public void resetPassword(String username) {
        try {
            authService.initiatePasswordReset(username);
        } catch (Exception e) {
            logger.error("Password reset failed", e);
            errorMessage.set(getMessage("login.error.password.reset"));
        }
    }

    public void clearFields() {
        username.set("");
        password.set("");
        errorMessage.set("");
        rememberMe.set(false);
    }

    private void checkPasskeyAvailability() {
        // Implement passkey availability check using a dedicated service if needed
        passkeyAvailable.set(false);
    }

    private String getClientIpAddress() {
        // Implement IP address retrieval logic
        return "127.0.0.1";
    }

    private String getMessage(String key) {
        // Implement message retrieval from ResourceBundle
        return key;
    }

    public void authenticateWithPasskey() {
    }
}