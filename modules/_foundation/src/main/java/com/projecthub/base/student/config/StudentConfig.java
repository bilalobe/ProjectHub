package com.projecthub.base.student.config;

import com.projecthub.base.student.domain.event.StudentDomainEvent;
import com.projecthub.base.student.domain.repository.StudentJpaRepository;
import com.projecthub.base.student.infrastructure.event.adapter.StudentEventAdapter;
import jakarta.validation.Validator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@EnableJpaRepositories(basePackageClasses = StudentJpaRepository.class)
public class StudentConfig {

    public StudentConfig() {
    }

    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public StudentEventAdapter studentEventAdapter(ApplicationEventPublisher eventPublisher) {
        return new StudentEventAdapter() {
            @Override
            public void publish(StudentDomainEvent event) {
                eventPublisher.publishEvent(event);
            }

            @Override
            public String getExchange() {
                return "student.exchange";
            }

            @Override
            public String getRoutingKey(StudentDomainEvent event) {
                return "student.routing.key";
            }
        };
    }
}
