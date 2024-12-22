package com.projecthub.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.FileNotFoundException;

/**
 * Configuration class for repository layer setup including JPA and CSV data sources.
 * Provides profile-based repository configuration for different data access strategies.
 *
 * @since 1.0.0
 */
@Configuration
public class RepositoryConfig {

    private RepositoryConfig() {
    }

    /**
     * JPA specific repository configuration class.
     * Enables JPA repositories and transaction management for the application.
     */
    @Configuration
    @Profile("jpa")
    @EnableJpaRepositories(basePackages = "com.projecthub.core.repositories.jpa")
    @EnableTransactionManagement
    static class JpaRepositoryConfig {
        // Additional JPA configurations if needed

        /**
         * Configures the repository populator for initial data loading from JSON.
         *
         * @return Factory bean for repository population
         * @throws FileNotFoundException if the data file is not found
         */
        @Bean
        public Jackson2RepositoryPopulatorFactoryBean repositoryPopulator() throws FileNotFoundException {
            Jackson2RepositoryPopulatorFactoryBean factory = new Jackson2RepositoryPopulatorFactoryBean();
            ClassPathResource dataResource = new ClassPathResource("data.json");
            if (!dataResource.exists()) {
                throw new IllegalStateException("Initial data file 'data.json' not found");
            }
            factory.setResources(new ClassPathResource[]{dataResource});
            return factory;
        }
    }

    @Configuration
    @Profile("csv")
    @EnableTransactionManagement
    static class CsvRepositoryConfig {

    }
}