package com.projecthub.core.services.ui.notification;

import javafx.scene.control.Alert;
import org.springframework.stereotype.Service;

/**
 * Service to manage application notifications.
 */
@Service
public class NotificationService {

    /**
     * Displays an information alert.
     *
     * @param title   the title of the alert
     * @param content the content message of the alert
     */
    public void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Displays a warning alert.
     *
     * @param title   the title of the alert
     * @param content the content message of the alert
     */
    public void showWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Displays an error alert.
     *
     * @param title   the title of the alert
     * @param content the content message of the alert
     */
    public void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}