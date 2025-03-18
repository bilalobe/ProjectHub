package com.projecthub.application.cohort

import org.springframework.modulith.core.NamedModule
import org.springframework.modulith.core.ApplicationModule
import org.springframework.modulith.core.AllowedDependencies
import org.springframework.context.annotation.Configuration

/**
 * Module declaration for Cohort feature.
 * This class explicitly defines the boundaries and allowed dependencies for the Cohort module.
 */
@Configuration
@NamedModule("cohort")
@ApplicationModule(
    displayName = "Cohort Management Module",
    allowedDependencies = ["domain", "school"]
)
@AllowedDependencies(
    // Cohort feature can only depend on these modules
    value = [
        "core",           // Core application services
        "school"          // School management for school references
    ]
)
class CohortModule