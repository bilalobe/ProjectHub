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

fun Application.configureTemplating() {
    install(Velocity) {
        setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath")
        setProperty("classpath.resource.loader.class", ClasspathResourceLoader::class.java.name)
    }
    install(Thymeleaf) {
        setTemplateResolver(ClassLoaderTemplateResolver().apply {
            prefix = "templates/thymeleaf/"
            suffix = ".html"
            characterEncoding = "utf-8"
        })
    }
    routing {
        get("/index") {
            val sampleUser = VelocityUser(1, "John")
            call.respond(VelocityContent("templates/index.vl", mapOf("user" to sampleUser)))
        }
        get("/html-thymeleaf") {
            call.respond(ThymeleafContent("index", mapOf("user" to ThymeleafUser(1, "user1"))))
        }
    }
}

data class VelocityUser(val id: Int, val name: String)

data class ThymeleafUser(val id: Int, val name: String)

