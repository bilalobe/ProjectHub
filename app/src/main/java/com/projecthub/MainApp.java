package com.projecthub;

import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) {
        ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages");
        String greeting = bundle.getString("greeting");
        logger.info("Greeting loaded: {}", greeting);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/views/MainView.fxml"));
        BorderPane root = loader.load();
        Scene scene = new Scene(root);

        // Load and apply the Gluon theme stylesheet
        URL gluonStylesheetURL = getClass().getResource("/com/gluonhq/charm/glisten/afterburner/glisten.css");
        if (gluonStylesheetURL != null) {
            scene.getStylesheets().add(gluonStylesheetURL.toExternalForm());
            logger.info("Gluon stylesheet applied.");
        } else {
            logger.error("Gluon stylesheet not found: /com/gluonhq/charm/glisten/afterburner/glisten.css");
        }

        // Load and apply the custom stylesheet
        URL customStylesheetURL = getClass().getResource("/ui/styles/styles.css");
        if (customStylesheetURL != null) {
            scene.getStylesheets().add(customStylesheetURL.toExternalForm());
            logger.info("Custom stylesheet applied.");
        } else {
            logger.error("Custom stylesheet not found: /ui/styles/styles.css");
        }

        primaryStage.setScene(scene);
        primaryStage.setTitle("ProjectHub");
        primaryStage.show();

        logger.info("Application started successfully.");
    }
}