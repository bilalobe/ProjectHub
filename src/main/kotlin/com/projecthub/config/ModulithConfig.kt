package com.projecthub.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.modulith.Modulith
import org.springframework.modulith.core.ApplicationModules
import org.springframework.modulith.events.DistributionProperties

/**
 * Configuration for Spring Modulith.
 * Sets up module boundaries and event distribution.
 */
@Configuration
class ModulithConfig {

    /**
     * Configure the application modules.
     */
    @Bean
    fun applicationModules(): ApplicationModules {
        return ApplicationModules.of(Modulith::class.java)
    }

    /**
     * Configure event distribution.
     */
    @Bean
    fun distributionProperties(): DistributionProperties {
        // Customize event distribution if needed
        return DistributionProperties()
    }

    /**
     * Module-specific configuration goes here
     */
}
