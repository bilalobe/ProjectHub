package com.projecthub.base.school.infrastructure.event.config;

import com.projecthub.base.shared.config.BaseRabbitMQConfig;
import com.projecthub.base.shared.config.RabbitMQProperties;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchoolRabbitMQConfig extends BaseRabbitMQConfig {

    public static final String SCHOOL_EXCHANGE = "school.exchange";
    public static final String SCHOOL_QUEUE = "school.queue";
    public static final String SCHOOL_ROUTING_KEY = "school.#";

    public SchoolRabbitMQConfig(final RabbitMQProperties properties) {
        super(properties);
    }

    @Bean
    public Queue schoolQueue() {
        return QueueBuilder.durable(SchoolRabbitMQConfig.SCHOOL_QUEUE).build();
    }

    @Bean
    public TopicExchange schoolExchange() {
        return ExchangeBuilder.topicExchange(SchoolRabbitMQConfig.SCHOOL_EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding schoolBinding(final Queue schoolQueue, final TopicExchange schoolExchange) {
        return BindingBuilder.bind(schoolQueue).to(schoolExchange).with(SchoolRabbitMQConfig.SCHOOL_ROUTING_KEY);
    }
}
