package com.projecthub.gradle.plugins.distribution

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.file.DuplicatesStrategy

class DistributionPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.plugins.apply('distribution')
        
        project.tasks.withType(Jar).configureEach { jarTask ->
            jarTask.manifest {
                attributes(
                    'Implementation-Title': project.name,
                    'Implementation-Version': project.version,
                    'Created-By': "${System.getProperty('java.version')} (${System.getProperty('java.vendor')})",
                    'Build-Timestamp': new Date().format("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                )
            }
            jarTask.duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
        
        project.distributions {
            main {
                contents {
                    from project.tasks.named('jar')
                    into('lib') {
                        from project.configurations.runtimeClasspath
                    }
                }
            }
        }
    }
}
