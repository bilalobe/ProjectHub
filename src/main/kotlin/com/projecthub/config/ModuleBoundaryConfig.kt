package com.projecthub.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.modulith.core.ApplicationModules
import org.springframework.modulith.docs.Documenter
import org.springframework.modulith.events.core.EventSerializer
import org.springframework.modulith.core.Modulith

/**
 * Configuration for Spring Modulith boundary enforcement.
 * This class defines the module boundaries and documentation settings for the ProjectHub application.
 */
@Configuration
@Modulith(
    sharedModules = [
        "foundation.config",  // common or config modules
        "foundation.events"    // shared event classes, if you must
        // etc.
    ]
)
class ModuleBoundaryConfig {

    /**
     * Provides access to the application's modules structure.
     */
    @Bean
    fun applicationModules(): ApplicationModules {
        return ApplicationModules.of(ApplicationModuleRoot::class.java)
    }

    /**
     * Configures the module documenter that generates documentation about module boundaries and dependencies.
     */
    @Bean
    fun modulesDocumenter(modules: ApplicationModules): Documenter {
        return Documenter(modules)
            .writeDocumentation()
            .writeModulesAsPlantUml()
    }

    /**
     * Custom event serializer for modulith events.
     */
    @Bean
    fun eventSerializer(): EventSerializer {
        return EventSerializer.DEFAULT
    }
}

/**
 * Marker class to define the root of our application modules.
 */
class ApplicationModuleRoot
