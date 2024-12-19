package com.projecthub.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

/**
 * Configuration properties for the desktop data source.
 */
@Data
@ConfigurationProperties(prefix = "spring.datasource.desktop")
public class DesktopDataSourceProperties
{
    private String url;
}
