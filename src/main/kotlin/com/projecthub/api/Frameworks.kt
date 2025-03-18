package com.projecthub

import com.fasterxml.jackson.databind.*
import com.kborowy.authprovider.firebase.firebase
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.projecthub.api.csv.csvModule
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

fun Application.configureFrameworks() {
    install(Koin) {
        slf4jLogger()
        modules(
            module {
                single<HelloService> {
                    HelloService {
                        println(environment.log.info("Hello, World!"))
                    }
                }
            },
            // Add our CSV module here
            csvModule
        )
    }
    install(RabbitMQ) {
        uri = "amqp://guest:guest@localhost:5672"
        defaultConnectionName = "default-connection"
        dispatcherThreadPollSize = 4
        tlsEnabled = false
    }

    rabbitmq {
        queueBind {
            queue = "dlq"
            exchange = "dlx"
            routingKey = "dlq-dlx"
            exchangeDeclare {
                exchange = "dlx"
                type = "direct"
            }
            queueDeclare {
                queue = "dlq"
                durable = true
            }
        }
    }

    rabbitmq {
        queueBind {
            queue = "test-queue"
            exchange = "test-exchange"
            routingKey = "test-routing-key"
            exchangeDeclare {
                exchange = "test-exchange"
                type = "direct"
            }
            queueDeclare {
                queue = "test-queue"
                arguments = mapOf(
                    "x-dead-letter-exchange" to "dlx",
                    "x-dead-letter-routing-key" to "dlq-dlx"
                )
            }
        }
    }

    routing {
        get("/rabbitmq") {
            rabbitmq {
                basicPublish {
                    exchange = "test-exchange"
                    routingKey = "test-routing-key"
                    message { "Hello Ktor!" }
                }
            }
            call.respondText("Hello RabbitMQ!")
        }


        rabbitmq {
            basicConsume {
                autoAck = true
                queue = "test-queue"
                dispatcher = Dispatchers.rabbitMQ
                coroutinePollSize = 100
                deliverCallback<String> { tag, message ->
                    log.info("Received message: $message")
                }
            }
        }
    }
}
