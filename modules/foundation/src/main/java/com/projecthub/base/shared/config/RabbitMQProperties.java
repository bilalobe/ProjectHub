package com.projecthub.base.shared.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.rabbitmq")
public class RabbitMQProperties {
    private String host = "localhost";
    private int port = 5672;
    private String username = "guest";
    private String password = "guest";
}
