package com.projecthub.core.services.ui.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class NavigationService {

    private static NavigationService instance;
    private StackPane viewContainer;

    private NavigationService() {
        // Private constructor to enforce singleton pattern
    }

    public static synchronized NavigationService getInstance() {
        if (instance == null) {
            instance = new NavigationService();
        }
        return instance;
    }

    public void setViewContainer(StackPane viewContainer) {
        this.viewContainer = viewContainer;
    }

    public void navigateTo(String fxmlPath) {
        if (viewContainer != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Node view = loader.load();
                viewContainer.getChildren().setAll(view);
            } catch (IOException e) {
                e.printStackTrace();
                // Handle loading error (e.g., show alert)
            }
        } else {
            System.err.println("ViewContainer not initialized.");
        }
    }
}
