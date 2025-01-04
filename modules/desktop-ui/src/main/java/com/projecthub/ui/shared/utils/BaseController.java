package com.projecthub.ui.shared.utils;

import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * BaseController provides common functionalities for all Controllers.
 */
public abstract class BaseController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final ResourceBundle resourceBundle;

    protected BaseController() {
        // Load the resource bundle based on the default locale
        Locale locale = Locale.getDefault();
        resourceBundle = ResourceBundle.getBundle("messages", locale);
    }

    /**
     * Shows an alert with the specified type, title, and message.
     *
     * @param alertType the type of the alert
     * @param title     the title of the alert
     * @param message   the message to display
     */
    protected void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Logs an error message with the specified exception.
     *
     * @param message the error message
     * @param e       the exception to log
     */
    protected void logError(String message, Exception e) {
        logger.error(message, e);
    }

    /**
     * Validates that a string is not null or empty.
     *
     * @param value      the string to validate
     * @param messageKey the key for the error message to display if validation fails
     * @return true if the string is valid, false otherwise
     */
    protected boolean validateString(String value, String messageKey) {
        if (value == null || value.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, getLocalizedMessage("validation.error"), getLocalizedMessage(messageKey));
            return false;
        }
        return true;
    }

    /**
     * Validates that an object is not null.
     *
     * @param value      the object to validate
     * @param messageKey the key for the error message to display if validation fails
     * @return true if the object is valid, false otherwise
     */
    protected boolean validateNotNull(Object value, String messageKey) {
        if (value == null) {
            showAlert(Alert.AlertType.ERROR, getLocalizedMessage("validation.error"), getLocalizedMessage(messageKey));
            return false;
        }
        return true;
    }

    /**
     * Retrieves a localized message based on the provided key.
     *
     * @param key the key for the message
     * @return the localized message
     */
    protected String getLocalizedMessage(String key) {
        return resourceBundle.getString(key);
    }
}