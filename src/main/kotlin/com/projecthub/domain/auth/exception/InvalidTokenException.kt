package com.projecthub.domain.auth.exception

class InvalidTokenException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)