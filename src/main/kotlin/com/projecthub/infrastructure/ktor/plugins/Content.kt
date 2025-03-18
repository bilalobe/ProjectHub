package com.projecthub.infrastructure.ktor.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.serialization.jackson.*
import io.ktor.http.*

fun Application.configureContent() {
    install(ContentNegotiation) {
        jackson()
    }

    install(Compression) {
        gzip {
            priority = 1.0
        }
        deflate {
            priority = 10.0
            minimumSize(1024)
        }
    }

    install(CachingHeaders) {
        options { call, outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.CSS -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60))
                ContentType.Application.JavaScript -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60))
                else -> null
            }
        }
    }
}
