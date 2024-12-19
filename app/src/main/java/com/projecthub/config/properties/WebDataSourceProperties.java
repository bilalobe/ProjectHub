package com.projecthub.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

@ConfigurationProperties(prefix = "spring.datasource.web")
@Data
/*
 * Configuration properties for the web data source.
 */
public class WebDataSourceProperties
{
    private String url;
    private String username;
    private String password;
}
