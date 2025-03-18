package com.projecthub.core.application.security.port.`in`

import com.projecthub.core.domain.model.security.User
import java.util.Optional

interface UserSecurityUseCase {
    fun getCurrentUser(): Optional<User>
    fun isAuthenticated(): Boolean
    fun hasPermission(permission: String): Boolean
}
