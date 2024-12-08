package com.projecthub;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.net.URL;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/projecthub/ui/views/MainView.fxml"));
        BorderPane root = loader.load();
        Scene scene = new Scene(root);

        // Load and apply the Gluon theme stylesheet
        URL gluonStylesheetURL = getClass().getResource("/com/gluonhq/charm/glisten/afterburner/glisten.css");
        if (gluonStylesheetURL != null) {
            scene.getStylesheets().add(gluonStylesheetURL.toExternalForm());
        } else {
            System.err.println("Gluon stylesheet not found: /com/gluonhq/charm/glisten/afterburner/glisten.css");
        }

        // Load and apply the custom stylesheet
        URL customStylesheetURL = getClass().getResource("/com/projecthub/ui/css/styles.css");
        if (customStylesheetURL != null) {
            scene.getStylesheets().add(customStylesheetURL.toExternalForm());
        } else {
            System.err.println("Custom stylesheet not found: /com/projecthub/ui/css/styles.css");
        }

        primaryStage.setScene(scene);
        primaryStage.setTitle("ProjectHub");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}