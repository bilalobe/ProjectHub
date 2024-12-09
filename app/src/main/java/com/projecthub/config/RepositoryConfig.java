package com.projecthub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
public class RepositoryConfig {

    @Configuration
    @Profile("jpa")
    @EnableJpaRepositories(basePackages = "com.projecthub.repository.jpa")
    @EnableTransactionManagement
    static class JpaRepositoryConfig {
        // Additional JPA configurations if needed

        @Bean
        public Jackson2RepositoryPopulatorFactoryBean repositoryPopulator() {
            Jackson2RepositoryPopulatorFactoryBean factory = new Jackson2RepositoryPopulatorFactoryBean();
            factory.setResources(new ClassPathResource[]{new ClassPathResource("data.json")});
            return factory;
        }
    }

    @Configuration
    @Profile("csv")
    @EnableTransactionManagement
    static class CsvRepositoryConfig {

    }
}