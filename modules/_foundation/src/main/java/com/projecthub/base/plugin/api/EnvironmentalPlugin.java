package com.projecthub.base.plugin.api;

import org.springframework.plugin.core.Plugin;
import org.springframework.security.access.annotation.Secured;

/**
 * Core interface for environmental monitoring plugins.
 * Integrates with Spring Security and Apache Fortress RBAC.
 */
public interface EnvironmentalPlugin extends Plugin<String> {
    
    @Secured("ROLE_ENVIRONMENTAL_READ")
    String getId();
    
    @Secured("ROLE_ENVIRONMENTAL_READ")
    String getName();
    
    @Secured("ROLE_ENVIRONMENTAL_READ")
    String getDescription();
    
    @Secured("ROLE_ENVIRONMENTAL_WRITE")
    void initialize();
    
    @Secured("ROLE_ENVIRONMENTAL_WRITE")
    void configure(PluginConfig config);
    
    @Secured("ROLE_ENVIRONMENTAL_READ")
    EnvironmentalData getCurrentReadings();
    
    @Secured("ROLE_ENVIRONMENTAL_WRITE")
    void shutdown();
}