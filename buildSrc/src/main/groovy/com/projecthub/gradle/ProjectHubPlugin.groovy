package com.projecthub.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class ProjectHubPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.apply from: "${project.rootProject.projectDir}/buildSrc/src/main/groovy/dependencies.gradle"
        project.apply from: "${project.rootProject.projectDir}/buildSrc/src/main/groovy/modulith.gradle"

        project.dependencies {
            implementation "org.pf4j:pf4j:${versions.pf4j}"
            implementation "org.pf4j:pf4j-spring:${versions.pf4jSpring}"
        }
    }
}