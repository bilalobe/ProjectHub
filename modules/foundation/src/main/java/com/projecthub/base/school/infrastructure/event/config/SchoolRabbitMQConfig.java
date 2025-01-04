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

    public SchoolRabbitMQConfig(RabbitMQProperties properties) {
        super(properties);
    }

    @Bean
    public Queue schoolQueue() {
        return QueueBuilder.durable(SCHOOL_QUEUE).build();
    }

    @Bean
    public TopicExchange schoolExchange() {
        return ExchangeBuilder.topicExchange(SCHOOL_EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding schoolBinding(Queue schoolQueue, TopicExchange schoolExchange) {
        return BindingBuilder.bind(schoolQueue).to(schoolExchange).with(SCHOOL_ROUTING_KEY);
    }
}
