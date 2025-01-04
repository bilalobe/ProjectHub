package com.projecthub.mobile.app

import android.app.Application
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class ProjectHubApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Spring context
    }
}
