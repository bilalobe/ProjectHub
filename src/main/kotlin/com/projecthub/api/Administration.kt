package com.projecthub.api

import com.fasterxml.jackson.databind.*
import com.kborowy.authprovider.firebase.firebase
import com.mongodb.kotlin.client.coroutine.MongoClient
import dev.inmo.krontab.builder.*
import io.github.damir.denis.tudor.ktor.server.rabbitmq.RabbitMQ
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.*
import io.github.damir.denis.tudor.ktor.server.rabbitmq.rabbitMQ
import io.github.flaxoos.ktor.server.plugins.ratelimiter.*
import io.github.flaxoos.ktor.server.plugins.ratelimiter.implementations.*
import io.github.flaxoos.ktor.server.plugins.taskscheduling.*
import io.github.flaxoos.ktor.server.plugins.taskscheduling.managers.lock.database.*
import io.github.flaxoos.ktor.server.plugins.taskscheduling.managers.lock.redis.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.jackson.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.ldap.*
import io.ktor.server.engine.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.csrf.*
import io.ktor.server.plugins.hsts.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.sse.*
import io.ktor.server.thymeleaf.Thymeleaf
import io.ktor.server.thymeleaf.ThymeleafContent
import io.ktor.server.velocity.*
import io.ktor.server.webjars.*
import io.ktor.server.websocket.*
import io.ktor.sse.*
import io.ktor.websocket.*
import io.micrometer.prometheus.*
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.apache.velocity.runtime.RuntimeConstants
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

fun Application.configureAdministration() {
    install(ShutDownUrl.ApplicationCallPlugin) {
        // The URL that will be intercepted (you can also use the application.conf's ktor.deployment.shutdown.url key)
        shutDownUrl = "/ktor/application/shutdown"
        // A function that will be executed to get the exit code of the process
        exitCodeSupplier = { 0 } // ApplicationCall.() -> Int
    }
    install(TaskScheduling) {
        // Choose task manager config based on your chosen task manager dependencies
        redis { // <-- given no name, this will be the default manager
            connectionPoolInitialSize = 1
            host = "host"
            port = 6379
            username = "my_username"
            password = "my_password"
            connectionAcquisitionTimeoutMs = 1_000
            lockExpirationMs = 60_000
        }
        jdbc("my jdbc manager") { // <-- given a name, a manager can be explicitly selected for a task
            database = org.jetbrains.exposed.sql.Database.connect(
                url = "jdbc:postgresql://host:port",
                driver = "org.postgresql.Driver",
                user = "my_username",
                password = "my_password"
            ).also {
                transaction { SchemaUtils.create(DefaultTaskLockTable) }
            }
        }
        mongoDb("my mongodb manager") {
            databaseName = "test"
            client = MongoClient.create("mongodb://host:port")
        }

        task { // if no taskManagerName is provided, the task would be assigned to the default manager
            name = "My task"
            task = { taskExecutionTime ->
                log.info("My task is running: $taskExecutionTime")
            }
            kronSchedule = {
                hours {
                    from(0).every(12)
                }
                minutes {
                    from(10).every(30)
                }
            }
            concurrency = 2
        }

        task(taskManagerName = "my jdbc manager") {
            name = "My Jdbc task"
            // rest of task config
        }
    }
    routing {
        route("/") {
            install(RateLimiting) {
                rateLimiter {
                    type = TokenBucket::class
                    capacity = 100
                    rate = 10.seconds
                }
            }
        }
    }
}

