package com.projecthub.ui.login.model

data class LoginCredentials(
    val username: String = "",
    val password: String = "",
    val authenticationType: String = "password"
)

data class LoginState(
    val credentials: LoginCredentials = LoginCredentials(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)

sealed class LoginEvent {
    data class UpdateUsername(val username: String) : LoginEvent()
    data class UpdatePassword(val password: String) : LoginEvent()
    object SubmitLogin : LoginEvent()
    object ClearError : LoginEvent()
}