package com.projecthub.config;

import com.projecthub.config.properties.DesktopDataSourceProperties;
import com.projecthub.config.properties.WebDataSourceProperties;
import com.projecthub.core.services.sync.SyncService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration class for managing DataSources based on application profiles.
 * Defines DataSource beans for 'web' and 'desktop' profiles.
 */
@Configuration
public class StorageConfig 
{

    private static final Logger logger = LoggerFactory.getLogger(StorageConfig.class);

    private final WebDataSourceProperties webProperties;
    private final DesktopDataSourceProperties desktopProperties;

    
    public StorageConfig(WebDataSourceProperties webProperties, DesktopDataSourceProperties desktopProperties)
    {
        this.webProperties = webProperties;
        this.desktopProperties = desktopProperties;
    }

    /**
     * Configures the DataSource for the 'web' profile using PostgreSQL.
     *
     * @return configured DataSource
     */
    @Profile("web")
    @Bean
    public DataSource webDataSource() 
    {
        logger.info("Initializing web DataSource with URL: {}", webProperties.getUrl());
        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .url(webProperties.getUrl())
                .username(webProperties.getUsername())
                .password(webProperties.getPassword())
                .build();
    }

    /**
     * Configures the DataSource for the 'desktop' profile using H2.
     *
     * @return configured DataSource
     */
    @Profile("desktop")
    @Bean
    public DataSource desktopDataSource()
    {
        logger.info("Initializing desktop DataSource with URL: {}", desktopProperties.getUrl());
        return DataSourceBuilder.create()
                .driverClassName("org.h2.Driver")
                .url(desktopProperties.getUrl())
                .username("sa")
                .password("")
                .build();
    }

    /**
     * Configures the SyncService bean using the active DataSource.
     *
     * @param dataSource the active DataSource based on the current profile
     * @return a new instance of SyncService
     */
    @Bean
    public SyncService syncService(DataSource dataSource) 
    {
        logger.info("Initializing SyncService with DataSource: {}", dataSource.getClass().getSimpleName());
        return new SyncService(dataSource);
    }
}