package com.projecthub;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ComponentScan(
    basePackages = "com.projecthub",
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = "com\\.projecthub\\.repository\\.(csv|jpa)\\..*"
    )
)
public class ProjectHubApplication {

    public ProjectHubApplication(Environment env) {
    }
}