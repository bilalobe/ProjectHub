package com.projecthub.gradle.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion

/**
 * Base convention plugin that applies common configuration to all module types
 */
class ModuleConventionPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        // Apply Java plugin if not already applied
        if (!project.plugins.hasPlugin('java')) {
            project.plugins.apply('java')
        }
        
        // Apply Spring Boot dependency management
        if (!project.plugins.hasPlugin('io.spring.dependency-management')) {
            project.plugins.apply('io.spring.dependency-management')
        }
        
        // Apply base dependencies
        project.ext.applyBaseDependencies(project)
        
        // Configure Java compilation
        project.tasks.withType(JavaCompile).configureEach {
            options.encoding = 'UTF-8'
            options.compilerArgs += ['-Xlint:unchecked', '-Xlint:deprecation']
        }
        
        // Configure test task
        project.tasks.withType(Test).configureEach {
            useJUnitPlatform()
            maxParallelForks = Runtime.runtime.availableProcessors().intdiv(2) ?: 1
            testLogging {
                events "passed", "skipped", "failed"
            }
        }
        
        // Configure Java toolchain
        project.java {
            toolchain {
                languageVersion = project.findProperty('javaVersion') ?: JavaLanguageVersion.of(17)
            }
        }
    }
}

/**
 * Web module convention that applies typical web module configuration
 */
class WebModuleConventionPlugin extends ModuleConventionPlugin {
    @Override
    void apply(Project project) {
        super.apply(project)
        
        // Apply Spring Boot plugin to generate executable JARs
        project.plugins.apply('org.springframework.boot')
        
        // Apply web-specific dependencies
        def webDepsApplier = project.rootProject.ext.applyWebDependencies
        if (webDepsApplier) {
            webDepsApplier(project)
        }
    }
}

/**
 * Library module convention for non-executable modules
 */
class LibraryModuleConventionPlugin extends ModuleConventionPlugin {
    @Override
    void apply(Project project) {
        super.apply(project)
        
        // Configure as a non-executable library
        project.tasks.named('bootJar').configure { task ->
            task.enabled = false
        }
        
        project.tasks.named('jar').configure { task ->
            task.enabled = true
        }
    }
}

/**
 * Data module convention for modules focused on persistence
 */
class DataModuleConventionPlugin extends LibraryModuleConventionPlugin {
    @Override
    void apply(Project project) {
        super.apply(project)
        
        // Apply persistence-specific dependencies
        def persistenceDepsApplier = project.rootProject.ext.applyPersistenceDependencies
        if (persistenceDepsApplier) {
            persistenceDepsApplier(project)
        }
    }
}

/**
 * Security module convention
 */
class SecurityModuleConventionPlugin extends LibraryModuleConventionPlugin {
    @Override
    void apply(Project project) {
        super.apply(project)
        
        // Apply security-specific dependencies
        def securityDepsApplier = project.rootProject.ext.applySecurityDependencies
        if (securityDepsApplier) {
            securityDepsApplier(project)
        }
    }
}

/**
 * UI module convention for modules with user interfaces
 */
class UiModuleConventionPlugin extends WebModuleConventionPlugin {
    @Override
    void apply(Project project) {
        super.apply(project)
        
        // Apply UI-specific dependencies
        def uiDepsApplier = project.rootProject.ext.applyUiDependencies
        if (uiDepsApplier) {
            uiDepsApplier(project)
        }
    }
}

/**
 * Gateway module convention for API gateways
 */
class GatewayModuleConventionPlugin extends WebModuleConventionPlugin {
    @Override
    void apply(Project project) {
        super.apply(project)
        
        // Ensure all gateway modules have security dependencies
        def securityDepsApplier = project.rootProject.ext.applySecurityDependencies
        if (securityDepsApplier) {
            securityDepsApplier(project)
        }
    }
}