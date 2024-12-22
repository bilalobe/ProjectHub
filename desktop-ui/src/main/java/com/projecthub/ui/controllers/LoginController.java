package com.projecthub.ui.controllers;

import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.projecthub.ui.viewmodels.LoginViewModel;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import org.springframework.stereotype.Component;

import static java.util.concurrent.CompletableFuture.runAsync;

@Component
public class LoginController extends BaseController {

    private final LoginViewModel loginViewModel;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox rememberMeCheckbox;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink forgotPasswordLink;

    @FXML
    private Label errorLabel;

    @FXML
    private ImageView logoImage;

    @FXML
    private Button passkeyButton;

    @FXML
    private Button githubLoginButton;

    @FXML
    private TabPane authTabPane;

    @FXML
    private ProgressIndicator loadingIndicator;

    private Node githubIcon;

    public LoginController(LoginViewModel loginViewModel) {
        this.loginViewModel = loginViewModel;
    }

    @FXML
    public void initialize() {
        bindProperties();
        setupEventHandlers();
        setupValidation();
        setupAuthMethods();
        setupLoadingStates();
        setupAccessibility();
    }

    private void bindProperties() {
        // Bind text fields to view model properties
        usernameField.textProperty().bindBidirectional(loginViewModel.usernameProperty());
        passwordField.textProperty().bindBidirectional(loginViewModel.passwordProperty());
        rememberMeCheckbox.selectedProperty().bindBidirectional(loginViewModel.rememberMeProperty());
        errorLabel.textProperty().bind(loginViewModel.errorMessageProperty());
        errorLabel.visibleProperty().bind(loginViewModel.errorMessageProperty().isNotEmpty());

        // Disable login button during authentication
        loginButton.disableProperty().bind(loginViewModel.loginInProgressProperty());
    }

    private void setupEventHandlers() {
        loginButton.setOnAction(event -> handleLogin());
        forgotPasswordLink.setOnAction(event -> handleForgotPassword());

        // Handle Enter key in password field
        passwordField.setOnAction(event -> handleLogin());
    }

    private void setupValidation() {
        // Add CSS classes for validation feedback
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
            usernameField.pseudoClassStateChanged(
                    getPseudoClass("error"),
                    newVal.trim().isEmpty()
            );
        });

        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            passwordField.pseudoClassStateChanged(
                    getPseudoClass("error"),
                    newVal.trim().isEmpty()
            );
        });
    }

    private PseudoClass getPseudoClass(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPseudoClass'");
    }

    private void setupAuthMethods() {
        // Setup passkey authentication
        passkeyButton.setDisable(!loginViewModel.passkeyAvailableProperty().get());
        passkeyButton.setOnAction(event -> handlePasskeyAuth());

        // Setup GitHub authentication
        githubIcon = MaterialDesignIcon.WEB.graphic();
        githubLoginButton.setOnAction(event -> handleGithubAuth());
    }

    private void setupLoadingStates() {
        loadingIndicator.visibleProperty().bind(loginViewModel.loginInProgressProperty());
        BooleanBinding loading = loginViewModel.loginInProgressProperty();

        // Disable all inputs during loading
        usernameField.disableProperty().bind(null);
        passwordField.disableProperty().bind(null);
        loginButton.disableProperty().bind(null);
        passkeyButton.disableProperty().bind(
                loading.or(loginViewModel.passkeyAvailableProperty().not())
        );
        githubLoginButton.disableProperty().bind(loading);
    }

    private void setupAccessibility() {
        usernameField.setAccessibleRoleDescription(getMessage("accessibility.username.input"));
        passwordField.setAccessibleRoleDescription(getMessage("accessibility.password.input"));
        loginButton.setAccessibleRoleDescription(getMessage("accessibility.login.button"));
        passkeyButton.setAccessibleRoleDescription(getMessage("accessibility.passkey.button"));
        githubLoginButton.setAccessibleRoleDescription(getMessage("accessibility.github.button"));
    }

    private String getMessage(String s) {
        return s;
    }

    private void handleLogin() {
        // Run login in background thread
        runAsync(() -> {
            boolean success = loginViewModel.login();
            if (success) {
                Platform.runLater(this::navigateToMainView);
            }
        });
    }

    private void handleForgotPassword() {
        String username = usernameField.getText();
        if (username.isEmpty()) {
            showAlert(Alert.AlertType.WARNING,
                    "Password Reset",
                    "Please enter your username first.");
            return;
        }

        loginViewModel.resetPassword(username);
        showAlert(Alert.AlertType.INFORMATION,
                "Password Reset",
                "If an account exists for this username, " +
                        "you will receive password reset instructions via email.");
    }

    private void handlePasskeyAuth() {
        runAsync(() -> {
            loginViewModel.authenticateWithPasskey();
            if (loginViewModel.isAuthenticated()) {
                Platform.runLater(this::navigateToMainView);
            }
        });
    }

    private void handleGithubAuth() {
        String authUrl = loginViewModel.githubAuthUrlProperty().get();
        getHostServices().showDocument(authUrl);
    }

    public void handleGithubCallback(String code) {
        runAsync(() -> {
            loginViewModel.handleGithubCallback(code);
            if (loginViewModel.isAuthenticated()) {
                Platform.runLater(this::navigateToMainView);
            }
        });
    }

    private void navigateToMainView() {
        // Navigate to main view after successful login
        navigate("/com/projecthub/ui/views/MainView.fxml");
    }

    @Override
    protected void onError(Throwable e) {
        String errorKey = switch (e) {
            case AuthenticationException ae -> "login.error.invalid";
            case RateLimitException re -> "login.error.rateLimit";
            case ServiceUnavailableException se -> "login.error.service";
            default -> "login.error.general";
        };
        Platform.runLater(() -> errorLabel.setText(getMessage(errorKey)));
    }
}
