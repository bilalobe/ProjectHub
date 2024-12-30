package com.projecthub.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class ProjectHubPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.plugins {
            id 'org.beryx.jlink'
            id 'org.springframework.boot'
            id 'io.spring.dependency-management'
            id 'com.netflix.dgs.codegen'
        }

        project.apply from: "${project.rootProject.projectDir}/buildSrc/src/main/groovy/dependencies.gradle"
        project.apply from: "${project.rootProject.projectDir}/buildSrc/src/main/groovy/modulith.gradle"

        project.dependencies {
            // Spring Boot Core
            implementation platform("org.springframework.boot:spring-boot-dependencies:${versions.springBoot}")
            implementation 'org.springframework.boot:spring-boot-starter'
            implementation 'org.springframework.boot:spring-boot-starter-validation'
            implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
            implementation 'org.springframework.boot:spring-boot-starter-web'
            implementation 'org.springframework.boot:spring-boot-starter-security'
            implementation 'org.springframework.boot:spring-boot-starter-amqp'

            // Netflix DGS
            implementation platform("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:${versions.dgs}")
            implementation 'com.netflix.graphql.dgs:graphql-dgs-spring-boot-starter'
            implementation 'com.netflix.graphql.dgs:graphql-dgs-subscriptions-websockets-starter'
            implementation 'com.netflix.graphql.dgs:graphql-dgs-graphiql'
            implementation "com.netflix.graphql.dgs:graphql-dgs-codegen-gradle:${versions.dgsCodegen}"

            // Spring Modulith
            implementation platform("org.springframework.modulith:spring-modulith-bom:${versions.springModulith}")
            implementation 'org.springframework.modulith:spring-modulith-core'
            implementation 'org.springframework.modulith:spring-modulith-starter-core'
            implementation 'org.springframework.modulith:spring-modulith-events'
            
            // Database
            implementation "com.zaxxer:HikariCP:${versions.hikari}"
            implementation "com.h2database:h2:${versions.h2}"
            
            // Tools & Utils
            implementation "org.jmolecules:jmolecules-ddd:${versions.jmolecules}"
            implementation "org.pf4j:pf4j:${versions.pf4j}"
            implementation "org.pf4j:pf4j-spring:${versions.pf4jSpring}"
            implementation "io.swagger.core.v3:swagger-annotations:${versions.swagger}"
            
            // Lombok
            compileOnly "org.projectlombok:lombok:${versions.lombok}"
            annotationProcessor "org.projectlombok:lombok:${versions.lombok}"
            
            // Testing
            testImplementation 'org.springframework.boot:spring-boot-starter-test'
            testImplementation "org.junit.jupiter:junit-jupiter:${versions.junit}"
            testImplementation "org.mockito:mockito-junit-jupiter:${versions.mockito}"
            testImplementation "org.springframework.modulith:spring-modulith-test:${versions.springModulith}"
        }

        project.bootRun {
            mainClass = 'com.projecthub.core.CoreApplication'
            systemProperty 'spring.profiles.active', 'dev'
            jvmArgs = ['-Xmx1024m']
        }

        project.generateJava {
            schemaPaths = ["${project.projectDir}/src/main/resources/schema/"]
            packageName = 'com.projecthub.core.graphql.generated'
            generateClient = true
        }

        project.tasks.register('startWeb') {
            group = 'application'
            description = 'Starts the web application'
            dependsOn 'bootRun'
        }
    }
}