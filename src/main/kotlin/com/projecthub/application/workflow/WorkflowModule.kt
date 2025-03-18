package com.projecthub.application.workflow

import org.springframework.modulith.core.NamedModule
import org.springframework.modulith.core.ApplicationModule
import org.springframework.modulith.core.AllowedDependencies
import org.springframework.context.annotation.Configuration

/**
 * Module declaration for Workflow feature.
 * This class explicitly defines the boundaries and allowed dependencies for the Workflow module.
 */
@Configuration
@NamedModule("workflow")
@ApplicationModule(
    displayName = "Workflow Management Module",
    allowedDependencies = ["domain", "core"]
)
@AllowedDependencies(
    // Workflow feature can only depend on these modules
    value = [
        "core"    // Core application services
    ]
)
class WorkflowModule