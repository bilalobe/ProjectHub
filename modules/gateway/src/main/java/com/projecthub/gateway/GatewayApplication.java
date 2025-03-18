package com.projecthub.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

import com.projecthub.auth.config.AuthModule;
import com.projecthub.auth.config.SharedCorsConfig;
import com.projecthub.auth.config.JwtConfig;
import com.projecthub.gateway.config.GatewayModule;

/**
 * Main entry point for the ProjectHub API Gateway.
 * 
 * <p>This gateway serves as the central routing and security enforcement point,
 * integrating with the auth module for authentication and authorization decisions.</p>
 */
@SpringBootApplication
@EnableDiscoveryClient
@Import({
    AuthModule.class,
    SharedCorsConfig.class,
    JwtConfig.class,
    GatewayModule.class
})
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}