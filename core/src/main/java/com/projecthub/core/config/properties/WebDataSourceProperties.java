package com.projecthub.core.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.datasource.web")
@Data
/*
 * Configuration properties for the web data source.
 */
public class WebDataSourceProperties {
    private String url;
    private String username;
    private String password;
}
