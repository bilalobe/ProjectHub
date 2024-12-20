package com.projecthub.core.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the desktop data source.
 */
@Data
@ConfigurationProperties(prefix = "spring.datasource.desktop")
public class DesktopDataSourceProperties {
    private String url;
}
