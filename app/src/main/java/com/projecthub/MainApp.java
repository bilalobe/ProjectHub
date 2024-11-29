package com.projecthub;

import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class MainApp extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() throws Exception {
        context = new SpringApplicationBuilder(ProjectHubApplication.class).run();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize your JavaFX scene here
        primaryStage.setTitle("ProjectHub");
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        context.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}