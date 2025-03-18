package com.projecthub.application.milestone

import org.springframework.modulith.core.NamedModule
import org.springframework.modulith.core.ApplicationModule
import org.springframework.modulith.core.AllowedDependencies
import org.springframework.context.annotation.Configuration

/**
 * Module declaration for Milestone feature.
 * This class explicitly defines the boundaries and allowed dependencies for the Milestone module.
 */
@Configuration
@NamedModule("milestone")
@ApplicationModule(
    displayName = "Milestone Management Module",
    allowedDependencies = ["domain", "project", "user"]
)
@AllowedDependencies(
    // Milestone feature can only depend on these modules
    value = [
        "core",           // Core application services
        "project",        // Project management for project references
        "user"           // User management for assignee references
    ]
)
class MilestoneModule