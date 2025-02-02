package com.projecthub.ui.core.services.navigation;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
public class NavigationController {

    private ApplicationContext applicationContext;

    @FXML
    private TreeView<String> navigationTreeView;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    public void initialize() {
        setupNavigationTree();
    }

    private void setupNavigationTree() {
        TreeItem<String> rootItem = new TreeItem<>("ProjectHub");

        TreeItem<String> cohortsItem = new TreeItem<>("Cohorts");
        TreeItem<String> teamsItem = new TreeItem<>("Teams");
        TreeItem<String> studentsItem = new TreeItem<>("Students");

        Collections.addAll(rootItem.getChildren(), cohortsItem, teamsItem, studentsItem);
        navigationTreeView.setRoot(rootItem);
        navigationTreeView.setShowRoot(false);

        navigationTreeView.getSelectionModel().selectedItemProperty().addListener(this::handleNavigationSelection);
    }

    private void handleNavigationSelection(ObservableValue<? extends TreeItem<String>> observable,
                                           TreeItem<String> oldValue,
                                           TreeItem<String> newValue) {
        if (newValue != null) {
            String selectedText = newValue.getValue();
            switch (selectedText) {
                case "Cohorts" -> loadView("/fxml/CohortDetails.fxml");
                case "Teams" -> loadView("/fxml/TeamDetails.fxml");
                case "Students" -> loadView("/fxml/StudentDetails.fxml");
                default -> {
                    // Handle other selections
                }
            }
        }
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(applicationContext::getBean);
            BorderPane viewPane = loader.load();
            mainBorderPane.setCenter(viewPane);
        } catch (IOException e) {
            // Log the error
            LoggerFactory.getLogger(NavigationController.class).error("Failed to load view: {}", fxmlPath, e);
            // Show error dialog to the user
            showErrorDialog("Error", "Failed to load the requested view.");
        }
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}