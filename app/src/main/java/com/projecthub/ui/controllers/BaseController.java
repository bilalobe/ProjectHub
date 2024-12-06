package com.projecthub.ui.controllers;

import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BaseController provides common functionalities for all Controllers.
 */
public abstract class BaseController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

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
     * @param value   the string to validate
     * @param message the error message to display if validation fails
     * @return true if the string is valid, false otherwise
     */
    protected boolean validateString(String value, String message) {
        if (value == null || value.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", message);
            return false;
        }
        return true;
    }

    /**
     * Validates that an object is not null.
     *
     * @param value   the object to validate
     * @param message the error message to display if validation fails
     * @return true if the object is valid, false otherwise
     */
    protected boolean validateNotNull(Object value, String message) {
        if (value == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", message);
            return false;
        }
        return true;
    }
}