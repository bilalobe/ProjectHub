package com.projecthub.ui.login.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.projecthub.base.auth.api.dto.LoginRequestDTO
import com.projecthub.base.auth.service.AuthenticationService
import com.projecthub.ui.login.model.LoginCredentials
import com.projecthub.ui.login.model.LoginEvent
import com.projecthub.ui.login.model.LoginState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authService: AuthenticationService,
    private val coroutineScope: CoroutineScope
) {
    var state by mutableStateOf(LoginState())
        private set

    private val _events = MutableSharedFlow<LoginEvent>()
    val events: SharedFlow<LoginEvent> = _events.asSharedFlow()

    init {
        coroutineScope.launch {
            _events.collect { event ->
                handleEvent(event)
            }
        }
    }

    private suspend fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.UpdateUsername -> updateUsername(event.username)
            is LoginEvent.UpdatePassword -> updatePassword(event.password)
            is LoginEvent.SubmitLogin -> submitLogin()
            is LoginEvent.ClearError -> clearError()
        }
    }

    private fun updateUsername(username: String) {
        state = state.copy(
            credentials = state.credentials.copy(username = username),
            error = null
        )
    }

    private fun updatePassword(password: String) {
        state = state.copy(
            credentials = state.credentials.copy(password = password),
            error = null
        )
    }

    private suspend fun submitLogin() {
        state = state.copy(isLoading = true, error = null)
        try {
            val credentials = state.credentials
            val request = LoginRequestDTO(
                username = credentials.username,
                password = credentials.password,
                authenticationType = credentials.authenticationType
            )
            
            val token = authService.authenticate(request)
            state = state.copy(
                isLoading = false,
                isAuthenticated = true,
                error = null
            )
        } catch (e: Exception) {
            state = state.copy(
                isLoading = false,
                error = e.message ?: "Authentication failed"
            )
        }
    }

    private fun clearError() {
        state = state.copy(error = null)
    }

    fun emitEvent(event: LoginEvent) {
        coroutineScope.launch {
            _events.emit(event)
        }
    }
}