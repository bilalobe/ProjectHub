pluginManagement {
    repositories {
        // ...existing code...
    }

    plugins {
        // ...existing code...
        kotlin("jvm") version "1.9.22"
        kotlin("plugin.spring") version "1.9.22"
        kotlin("plugin.jpa") version "1.9.22"
    }
}

// ...existing code...

rootProject.name = "ProjectHub"

// Include foundation module with Kotlin support
include("foundation")
// ...existing code...
