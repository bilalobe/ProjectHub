package com.projecthub.domain.notification.exception

class ResourceNotFoundException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)