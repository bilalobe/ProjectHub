package com.projecthub.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

@Configuration
public class RepositoryConfig {

    @Configuration
    @Profile("jpa")
    @EnableJpaRepositories(basePackages = "com.projecthub.repository.jpa")
    static class JpaRepositoryConfig {
        
    }

    @Configuration
    @Profile("csv")
    static class CsvRepositoryConfig {
        // Configuration for CSV repositories if needed

        @Bean
        public Jackson2RepositoryPopulatorFactoryBean repositoryPopulator() {
            Jackson2RepositoryPopulatorFactoryBean factory = new Jackson2RepositoryPopulatorFactoryBean();
            factory.setResources(new ClassPathResource[]{new ClassPathResource("data.json")});
            return factory;
        }
    }
}
