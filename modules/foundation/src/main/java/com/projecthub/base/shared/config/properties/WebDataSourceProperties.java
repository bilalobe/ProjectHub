package com.projecthub.base.shared.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "spring.datasource.web")
@Data
@Component
/*
 * Configuration properties for the web data source.
 */
public class WebDataSourceProperties {
    private String url;
    private String username;
    private String password;
}

