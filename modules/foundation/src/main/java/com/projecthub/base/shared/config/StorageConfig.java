package com.projecthub.base.shared.config;

import com.projecthub.base.shared.config.properties.DesktopDataSourceProperties;
import com.projecthub.base.shared.config.properties.WebDataSourceProperties;
import com.projecthub.base.sync.application.service.SyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * Configuration class for managing DataSources based on application profiles.
 * Defines DataSource beans for 'web' and 'desktop' profiles.
 */
@Configuration
public class StorageConfig {

    private static final Logger logger = LoggerFactory.getLogger(StorageConfig.class);

    private final WebDataSourceProperties webProperties;
    private final DesktopDataSourceProperties desktopProperties;


    public StorageConfig(final WebDataSourceProperties webProperties, final DesktopDataSourceProperties desktopProperties) {
        this.webProperties = webProperties;
        this.desktopProperties = desktopProperties;
    }

    /**
     * Configures the DataSource for the 'web' profile using PostgresSQL.
     *
     * @return configured DataSource
     */
    @Profile("web")
    @Bean
    public DataSource webDataSource() {
        StorageConfig.logger.info("Initializing web DataSource with URL: {}", this.webProperties.getUrl());
        return DataSourceBuilder.create()
            .driverClassName("org.postgresql.Driver")
            .url(this.webProperties.getUrl())
            .username(this.webProperties.getUsername())
            .password(this.webProperties.getPassword())
            .build();
    }

    /**
     * Configures the DataSource for the 'desktop' profile using H2.
     *
     * @return configured DataSource
     */
    @Profile("desktop")
    @Bean
    public DataSource desktopDataSource() {
        StorageConfig.logger.info("Initializing desktop DataSource with URL: {}", this.desktopProperties.getUrl());
        return DataSourceBuilder.create()
            .driverClassName("org.h2.Driver")
            .url(this.desktopProperties.getUrl())
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
    public SyncService syncService(final DataSource dataSource) {
        StorageConfig.logger.info("Initializing SyncService with DataSource: {}", dataSource.getClass().getSimpleName());
        return new SyncService(dataSource);
    }
}
