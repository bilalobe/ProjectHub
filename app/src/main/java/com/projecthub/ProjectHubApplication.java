package com.projecthub;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.projecthub.config.CSVProperties;

@SpringBootApplication
@EnableConfigurationProperties(CSVProperties.class)
public class ProjectHubApplication {
}