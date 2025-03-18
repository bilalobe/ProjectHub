package com.projecthub.base.project.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "projecthub.project")
@Data
public class ProjectConfig {
    /**
     * Maximum number of active projects per team
     */
    private int maxActiveProjectsPerTeam = 10;
    
    /**
     * Default project status when created
     */
    private String defaultStatus = "CREATED";
    
    /**
     * Whether to enable project templates
     */
    private boolean templatesEnabled = true;
    
    /**
     * Maximum days a project can be overdue before escalation
     */
    private int maxOverdueDays = 7;
    
    /**
     * Whether to enable automatic status transitions
     */
    private boolean autoStatusTransitions = true;
    
    /**
     * Cache configuration for project data
     */
    private CacheConfig cache = new CacheConfig();
    
    @Data
    public static class CacheConfig {
        private boolean enabled = true;
        private int timeToLiveSeconds = 3600;
        private int maxSize = 1000;
    }
}