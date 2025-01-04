package com.projecthub.base.shared.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    public static final String SCHOOL_EXCHANGE = "school.exchange";
    public static final String SCHOOL_QUEUE = "school.queue";
    public static final String SCHOOL_ROUTING_KEY = "school.#";
    public static final String AUTH_EXCHANGE = "auth.exchange";
    public static final String AUTH_QUEUE = "auth.queue";
    public static final String AUTH_ROUTING_KEY = "auth.#";
    public static final String TASK_EXCHANGE = "task.exchange";
    public static final String TASK_QUEUE = "task.queue";
    public static final String TASK_ROUTING_KEY = "task.#";
    @Value("${spring.rabbitmq.host:localhost}")
    private String host;
    @Value("${spring.rabbitmq.port:5672}")
    private int port;
    @Value("${spring.rabbitmq.username:guest}")
    private String username;
    @Value("${spring.rabbitmq.password:guest}")
    private String password;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        return factory;
    }

    @Bean
    public Queue schoolQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 86400000); // 24 hours
        args.put("x-dead-letter-exchange", "school.dlx");
        return QueueBuilder.durable(SCHOOL_QUEUE)
            .withArguments(args)
            .build();
    }

    @Bean
    public TopicExchange schoolExchange() {
        return ExchangeBuilder.topicExchange(SCHOOL_EXCHANGE)
            .durable(true)
            .build();
    }

    @Bean
    public Binding schoolBinding(Queue schoolQueue, TopicExchange schoolExchange) {
        return BindingBuilder.bind(schoolQueue)
            .to(schoolExchange)
            .with(SCHOOL_ROUTING_KEY);
    }

    @Bean
    public Queue authQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 86400000);
        args.put("x-dead-letter-exchange", "auth.dlx");
        return QueueBuilder.durable(AUTH_QUEUE)
            .withArguments(args)
            .build();
    }

    @Bean
    public TopicExchange authExchange() {
        return ExchangeBuilder.topicExchange(AUTH_EXCHANGE)
            .durable(true)
            .build();
    }

    @Bean
    public Binding authBinding(Queue authQueue, TopicExchange authExchange) {
        return BindingBuilder.bind(authQueue)
            .to(authExchange)
            .with(AUTH_ROUTING_KEY);
    }

    @Bean
    public Queue taskQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 86400000);
        args.put("x-dead-letter-exchange", "task.dlx");
        return QueueBuilder.durable(TASK_QUEUE)
            .withArguments(args)
            .build();
    }

    @Bean
    public TopicExchange taskExchange() {
        return ExchangeBuilder.topicExchange(TASK_EXCHANGE)
            .durable(true)
            .build();
    }

    @Bean
    public Binding taskBinding(Queue taskQueue, TopicExchange taskExchange) {
        return BindingBuilder.bind(taskQueue)
            .to(taskExchange)
            .with(TASK_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setRetryTemplate(RetryTemplate.builder()
            .maxAttempts(3)
            .fixedBackoff(1000)
            .build());
        return template;
    }
}
