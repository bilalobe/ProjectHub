package com.projecthub.gradle.plugins.distribution

import org.gradle.api.Plugin
import org.gradle.api.Project

class ProjectHubDistPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.tasks.register('createGluonExecutable') {
            group = 'distribution'
            description = 'Creates native executable using Gluon'
            dependsOn 'gluonfxNative'
        }

        project.tasks.register('createWindowsInstaller') {
            group = 'distribution'
            description = 'Creates Windows installer using jpackage'
            dependsOn 'jpackage'
        }

        project.tasks.register('createAllExecutables') {
            group = 'distribution'
            description = 'Creates both Gluon native and Windows installer'
            dependsOn 'createGluonExecutable', 'createWindowsInstaller'
        }
    }
}
