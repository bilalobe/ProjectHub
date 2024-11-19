package com.projecthub.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.init.JacksonRepositoryPopulatorFactoryBean;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class RepositoryConfig {

    @Configuration
    @Profile("jpa")
    @EnableJpaRepositories(basePackages = "com.projecthub.repository.jpa")
    static class JpaRepositoryConfig {
        // Configuration for JPA repositories if needed
    }

    @Configuration
    @Profile("csv")
    static class CsvRepositoryConfig {
        // Configuration for CSV repositories if needed

        @Bean
        public JacksonRepositoryPopulatorFactoryBean repositoryPopulator() {
            JacksonRepositoryPopulatorFactoryBean factory = new JacksonRepositoryPopulatorFactoryBean();
            factory.setResources(new ClassPathResource[]{new ClassPathResource("data.json")});
            return factory;
        }
    }
}