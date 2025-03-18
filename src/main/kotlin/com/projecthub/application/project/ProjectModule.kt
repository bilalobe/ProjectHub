package com.projecthub.application.project

import org.springframework.modulith.core.NamedModule
import org.springframework.modulith.core.ApplicationModule
import org.springframework.modulith.core.AllowedDependencies
import org.springframework.context.annotation.Configuration

/**
 * Module declaration for Project feature.
 * This class explicitly defines the boundaries and allowed dependencies for the Project module.
 */
@Configuration
@NamedModule("project")
@ApplicationModule(
    displayName = "Project Management Module",
    allowedDependencies = ["domain", "workflow"]
)
@AllowedDependencies(
    // Project feature can only depend on these modules
    value = [
        "core",           // Core application services
        "workflow",       // Workflow management
        "user"            // User management for owner references 
    ]
)
class ProjectModule