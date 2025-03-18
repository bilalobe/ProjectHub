package com.projecthub.base.submission.infrastructure.event.config;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.retry.RetryOperationsInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SubmissionRetryConfig {

    public SubmissionRetryConfig() {
    }

    @Bean
    public RetryOperationsInterceptor submissionRetryInterceptor() {
        return RetryInterceptorBuilder.stateless()
            .maxAttempts(3)
            .backOffOptions(1000, 2.0, 10000)
            .build();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory submissionListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAdviceChain(submissionRetryInterceptor());
        return factory;
    }
}
