package com.projecthub.bootstrap

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

/**
 * Main application class for the foundation module.
 *
 * This class serves as the entry point for the application and configures component scanning
 * to include all the necessary packages for our hexagonal architecture.
 */
@SpringBootApplication
@ComponentScan(
    basePackages = [
        "com.projecthub.bootstrap",
        "com.projecthub.core",
        "com.projecthub.infrastructure"
    ]
)
@EntityScan("com.projecthub.infrastructure.persistence.jpa")
@EnableJpaRepositories("com.projecthub.infrastructure.persistence.jpa")
class ModulithApplication

/**
 * Main function that starts the Spring Boot application.
 *
 * @param args Command line arguments
 */
fun main(args: Array<String>) {
    runApplication<ModulithApplication>(*args)
}
