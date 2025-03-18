package com.projecthub.domain.notification.exception

class EmailSendingException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)