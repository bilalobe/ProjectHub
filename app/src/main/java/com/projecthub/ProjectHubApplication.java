package com.projecthub;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.projecthub.config.CsvProperties;

@SpringBootApplication
@EnableConfigurationProperties(CsvProperties.class)
public class ProjectHubApplication {
}