package com.projecthub.interfaces.ui.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Instant

/**
 * Manages authentication tokens and login state
 */
class TokenManager {
    private val _authState = MutableStateFlow<AuthState>(AuthState.NotAuthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private var accessToken: String? = null
    private var refreshToken: String? = null
    private var tokenExpirationTime: Instant? = null

    /**
     * Update authentication tokens after successful login
     *
     * @param accessToken JWT access token
     * @param refreshToken JWT refresh token for reauthorization
     * @param expiresIn Token expiration in seconds
     */
    fun setTokens(accessToken: String, refreshToken: String, expiresIn: Long) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.tokenExpirationTime = Instant.now().plusSeconds(expiresIn)
        _authState.value = AuthState.Authenticated(getCurrentUser(accessToken))
    }

    /**
     * Check if the current token is valid
     *
     * @return true if the access token is valid and not expired
     */
    fun isTokenValid(): Boolean {
        val token = accessToken
        val expiration = tokenExpirationTime
        
        return token != null && expiration != null && Instant.now().isBefore(expiration)
    }

    /**
     * Get the current access token if valid
     *
     * @return access token or null if not authenticated
     */
    fun getAccessToken(): String? {
        return if (isTokenValid()) accessToken else null
    }

    /**
     * Get the refresh token
     *
     * @return refresh token or null if not authenticated
     */
    fun getRefreshToken(): String? {
        return refreshToken
    }

    /**
     * Clear all authentication data on logout
     */
    fun clearTokens() {
        accessToken = null
        refreshToken = null
        tokenExpirationTime = null
        _authState.value = AuthState.NotAuthenticated
    }
    
    /**
     * Extract user information from JWT token
     */
    private fun getCurrentUser(token: String): User {
        // In a real implementation, decode the JWT token and extract user info
        // For now, we'll return a placeholder
        return User(
            id = "user-123",
            username = "johndoe",
            email = "john.doe@example.com",
            roles = listOf("USER")
        )
    }
}

/**
 * Represents the current authentication state
 */
sealed class AuthState {
    /**
     * User is not authenticated
     */
    object NotAuthenticated : AuthState()
    
    /**
     * Authentication is in progress
     */
    object InProgress : AuthState()
    
    /**
     * User is authenticated with the given user info
     */
    data class Authenticated(val user: User) : AuthState()
    
    /**
     * Authentication failed with the given error
     */
    data class Failed(val error: String) : AuthState()
}

/**
 * User model with basic information
 */
data class User(
    val id: String,
    val username: String,
    val email: String,
    val roles: List<String>
)