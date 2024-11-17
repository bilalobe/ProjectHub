package com.projecthub.config;

import javafx.fxml.FXMLLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public FXMLLoader fxmlLoader() {
        return new FXMLLoader();
    }
}