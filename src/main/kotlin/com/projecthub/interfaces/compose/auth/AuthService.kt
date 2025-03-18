package com.projecthub.interfaces.ui.auth

import com.projecthub.interfaces.ui.common.ResourceState
import com.projecthub.interfaces.ui.network.ApiClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Service that handles authentication with the backend
 */
class AuthService(
    private val apiClient: ApiClient,
    private val tokenManager: TokenManager
) {
    /**
     * Attempt to log in with username and password
     *
     * @param username User's username or email
     * @param password User's password
     * @return Flow of AuthState representing the authentication process
     */
    fun login(username: String, password: String): Flow<AuthState> = flow {
        emit(AuthState.InProgress)
        
        try {
            val loginRequest = LoginRequest(username, password)
            val result = apiClient.post<LoginRequest, LoginResponse>("/auth/login", loginRequest)
            
            when (result) {
                is ResourceState.Success -> {
                    val response = result.data
                    tokenManager.setTokens(
                        accessToken = response.accessToken,
                        refreshToken = response.refreshToken,
                        expiresIn = response.expiresIn
                    )
                    emit(tokenManager.authState.value)
                }
                is ResourceState.Error -> {
                    emit(AuthState.Failed(result.message))
                }
                else -> {
                    emit(AuthState.Failed("Unknown error occurred"))
                }
            }
        } catch (e: Exception) {
            emit(AuthState.Failed(e.message ?: "Login failed"))
        }
    }
    
    /**
     * Refresh the access token using the refresh token
     *
     * @return Flow of AuthState representing the token refresh process
     */
    fun refreshToken(): Flow<AuthState> = flow {
        emit(AuthState.InProgress)
        
        val refreshToken = tokenManager.getRefreshToken()
        if (refreshToken == null) {
            emit(AuthState.NotAuthenticated)
            return@flow
        }
        
        try {
            val refreshRequest = RefreshTokenRequest(refreshToken)
            val result = apiClient.post<RefreshTokenRequest, LoginResponse>("/auth/refresh", refreshRequest)
            
            when (result) {
                is ResourceState.Success -> {
                    val response = result.data
                    tokenManager.setTokens(
                        accessToken = response.accessToken,
                        refreshToken = response.refreshToken,
                        expiresIn = response.expiresIn
                    )
                    emit(tokenManager.authState.value)
                }
                is ResourceState.Error -> {
                    tokenManager.clearTokens()
                    emit(AuthState.NotAuthenticated)
                }
                else -> {
                    tokenManager.clearTokens()
                    emit(AuthState.NotAuthenticated)
                }
            }
        } catch (e: Exception) {
            tokenManager.clearTokens()
            emit(AuthState.NotAuthenticated)
        }
    }
    
    /**
     * Log the current user out
     *
     * @return Flow of AuthState representing the logout process
     */
    fun logout(): Flow<AuthState> = flow {
        emit(AuthState.InProgress)
        
        val accessToken = tokenManager.getAccessToken()
        if (accessToken != null) {
            try {
                apiClient.post<Unit, Unit>("/auth/logout", Unit)
            } catch (e: Exception) {
                // Ignore errors during logout
            }
        }
        
        tokenManager.clearTokens()
        emit(AuthState.NotAuthenticated)
    }
    
    /**
     * Get the current authentication state
     */
    val currentAuthState: Flow<AuthState> = tokenManager.authState
}

/**
 * Request body for login
 */
data class LoginRequest(
    val username: String,
    val password: String
)

/**
 * Request body for token refresh
 */
data class RefreshTokenRequest(
    val refreshToken: String
)

/**
 * Response from login or refresh token endpoint
 */
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val tokenType: String = "Bearer"
)