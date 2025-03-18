package com.projecthub.base.school.config;

import com.projecthub.base.school.domain.validation.SchoolValidator;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories
@EnableTransactionManagement
@EntityScan(basePackages = "com.projecthub.base.school.domain")
public class SchoolModuleConfig {

    public SchoolModuleConfig() {
    }

    @Bean
    public SchoolValidator schoolValidator() {
        return new SchoolValidator();
    }

    @Bean
    public ModelMapper schoolModelMapper() {
        return new ModelMapper();
    }
}
