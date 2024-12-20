package com.projecthub.core.services.ui.snackbar;

import com.gluonhq.charm.glisten.control.Snackbar;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class SnackbarService {

    private Snackbar snackbar;

    private SnackbarService() {
        // Private constructor to enforce singleton pattern
    }

    public static SnackbarService getInstance() {
        return SnackbarServiceHolder.INSTANCE;
    }

    public void setSnackbar(Snackbar snackbar) {
        this.snackbar = snackbar;
    }

    public void showMessage(String message) {
        if (snackbar == null) {
            System.err.println("Snackbar not initialized. Message: " + message);
        } else {
            Platform.runLater(() -> {
                snackbar.setMessage(message);
                snackbar.setActionText("OK");
                snackbar.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        // Handle the action event if needed
                    }
                });
                snackbar.show();
            });
        }
    }

    private static class SnackbarServiceHolder {
        private static final SnackbarService INSTANCE = new SnackbarService();
    }
}