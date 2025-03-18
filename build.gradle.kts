plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.jpa") version "1.9.22"
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.projecthub.module-base")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    
    // Domain dependencies
    implementation("org.jmolecules:jmolecules-ddd:${project.property("versions.jmolecules")}")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    
    // Event dependencies
    implementation("org.springframework.boot:spring-boot-starter-integration")
    implementation("org.springframework.integration:spring-integration-kotlin")
    
    implementation("io.ktor:ktor-server-core:2.0.0")
    implementation("io.ktor:ktor-server-auth:2.0.0")
    implementation("io.ktor:ktor-server-websockets:2.0.0")
    implementation("io.ktor:ktor-server-sse:2.0.0")
    implementation("io.ktor:ktor-client-core:2.0.0")
    implementation("io.ktor:ktor-client-cio:2.0.0")
    implementation("io.ktor:ktor-client-content-negotiation:2.0.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.0")
    implementation("io.ktor:ktor-server-content-negotiation:2.0.0")
    implementation("io.ktor:ktor-server-status-pages:2.0.0")
    implementation("io.ktor:ktor-server-call-logging:2.0.0")
    implementation("io.ktor:ktor-server-metrics-micrometer:2.0.0")
    implementation("io.micrometer:micrometer-registry-prometheus:1.8.0")
    implementation("com.rabbitmq:amqp-client:5.14.2")
    implementation("io.ktor:ktor-server-openapi:2.0.0")
    implementation("io.ktor:ktor-server-swagger:2.0.0")

    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}
