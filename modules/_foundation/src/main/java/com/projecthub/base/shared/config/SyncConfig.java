package com.projecthub.base.shared.config;

import com.projecthub.base.sync.application.service.LocalDataService;
import com.projecthub.base.sync.application.service.NetworkStatusChecker;
import com.projecthub.base.sync.application.service.RemoteDataService;
import com.projecthub.base.sync.application.service.SyncStatusTracker;
import com.projecthub.base.sync.application.service.impl.H2LocalDataService;
import com.projecthub.base.sync.application.service.impl.PostgresRemoteDataService;
import com.projecthub.base.sync.application.service.impl.SyncStatusTrackerImpl;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NonNls;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableScheduling
@EnableTransactionManagement
@EnableRetry
@Profile("desktop")
public class SyncConfig {

    @Value("${sync.remote.url}")
    private String remoteUrl;
    @Value("${sync.remote.username}")
    private String remoteUsername;
    @Value("${sync.remote.password}")
    private String remotePassword;
    @Value("${sync.local.path}")
    private String localDbPath;

    public SyncConfig() {
        // Default constructor for Spring configuration
    }

    @Bean(name = "localDataSource")
    public DataSource localDataSource() {
        @NonNls final HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:file:" + this.localDbPath);
        config.setDriverClassName("org.h2.Driver");
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        return new HikariDataSource(config);
    }

    @Bean(name = "remoteDataSource")
    public DataSource remoteDataSource() {
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl(this.remoteUrl);
        config.setUsername(this.remoteUsername);
        config.setPassword(this.remotePassword);
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(3);
        config.setMinimumIdle(0);
        config.setConnectionTimeout(5000L);
        return new HikariDataSource(config);
    }

    @Bean
    public NetworkStatusChecker networkStatusChecker() {
        return new NetworkStatusChecker();
    }

    @Bean
    public SyncStatusTracker syncStatusTracker() {
        return new SyncStatusTrackerImpl();
    }

    @Bean
    public LocalDataService localDataService(@Qualifier("localDataSource") final DataSource localDataSource) {
        return new H2LocalDataService(localDataSource);
    }

    @Bean
    public RemoteDataService remoteDataService(@Qualifier("remoteDataSource") final DataSource remoteDataSource) {
        return new PostgresRemoteDataService(remoteDataSource);
    }
}
