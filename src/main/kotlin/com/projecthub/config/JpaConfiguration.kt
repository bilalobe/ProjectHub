package com.projecthub.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.*

/**
 * Configuration for JPA repositories and auditing.
 */
@Configuration
@EnableJpaRepositories(basePackages = ["com.projecthub"])
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableTransactionManagement
class JpaConfiguration {

    /**
     * Bean that provides the current auditor (user) for entity auditing.
     */
    @Bean
    fun auditorProvider(): AuditorAware<UUID> {
        return AuditorAware {
            // In a real application, this would return the current user's ID
            // For now, just return an empty optional
            Optional.empty()
        }
    }
}
