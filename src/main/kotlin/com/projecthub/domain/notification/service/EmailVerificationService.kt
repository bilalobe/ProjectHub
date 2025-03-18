package com.projecthub.domain.notification.service

import com.projecthub.domain.auth.exception.InvalidTokenException
import com.projecthub.domain.notification.exception.ResourceNotFoundException
import com.projecthub.domain.user.User
import com.projecthub.domain.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class EmailVerificationService(
    private val emailService: EmailService,
    private val userRepository: UserRepository,
    @Value("\${app.base-url}") private val baseUrl: String
) {
    companion object {
        private const val VERIFICATION_TEMPLATE = "email/verification"
        private const val TOKEN_EXPIRY_HOURS = 24
        private val logger = LoggerFactory.getLogger(EmailVerificationService::class.java)
    }

    /**
     * Sends a verification email to a user
     */
    @Transactional
    suspend fun sendVerificationEmail(user: User) = withContext(Dispatchers.IO) {
        logger.info("Sending verification email to user: {}", user.email)

        val token = generateVerificationToken()
        val updatedUser = user.copy(
            verificationToken = token,
            verificationTokenExpiry = LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS.toLong())
        )

        userRepository.save(updatedUser)

        val templateVars = buildTemplateVariables(updatedUser, token)
        sendVerificationEmailTemplate(updatedUser, templateVars)
    }

    private suspend fun sendVerificationEmailTemplate(
        user: User,
        templateVars: Map<String, Any>
    ) {
        emailService.sendTemplatedEmail(
            to = user.email,
            subject = "Verify your email address",
            template = VERIFICATION_TEMPLATE,
            variables = templateVars
        )
    }

    private fun buildTemplateVariables(user: User, token: String) = mapOf(
        "name" to user.firstName,
        "verificationLink" to "$baseUrl/verify?token=$token",
        "expiryHours" to TOKEN_EXPIRY_HOURS
    )

    /**
     * Verifies a user's email using their verification token
     */
    @Transactional
    suspend fun verifyEmail(token: String) = withContext(Dispatchers.IO) {
        logger.info("Processing email verification token: {}", token)

        val user = userRepository.findByVerificationToken(token)
            ?: throw InvalidTokenException("Invalid verification token")

        validateVerification(user)

        val verifiedUser = user.copy(
            verified = true,
            enabled = true,
            verificationToken = null,
            verificationTokenExpiry = null
        )

        userRepository.save(verifiedUser)
        logger.info("Email verified successfully for user: {}", verifiedUser.email)
    }

    private fun validateVerification(user: User) {
        if (user.verified) {
            logger.warn("Attempted to verify already verified user: {}", user.username)
            throw InvalidTokenException("Email already verified")
        }

        if (user.verificationTokenExpiry == null ||
            user.verificationTokenExpiry.isBefore(LocalDateTime.now())
        ) {
            logger.error("Verification token expired for user: {}", user.username)
            throw InvalidTokenException("Verification token has expired")
        }

        if (user.verificationToken == null) {
            logger.error("No verification token found for user: {}", user.username)
            throw InvalidTokenException("Invalid verification token")
        }
    }

    /**
     * Resends verification email to a user
     */
    @Transactional
    suspend fun resendVerificationEmail(email: String) = withContext(Dispatchers.IO) {
        logger.info("Resending verification email to: {}", email)

        val user = userRepository.findByEmail(email)
            ?: throw ResourceNotFoundException("User not found with email: $email")

        if (user.verified) {
            logger.warn("Attempted to resend verification email for already verified user: {}", email)
            return@withContext
        }

        val refreshedUser = user.copy(
            verificationToken = generateVerificationToken(),
            verificationTokenExpiry = LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS.toLong())
        )

        userRepository.save(refreshedUser)
        val templateVars = buildTemplateVariables(refreshedUser, refreshedUser.verificationToken!!)
        sendVerificationEmailTemplate(refreshedUser, templateVars)
    }

    private fun generateVerificationToken(): String = UUID.randomUUID().toString()

    /**
     * Scheduled task to clean up expired verification tokens
     */
    @Scheduled(cron = "0 0 2 * * *") // Run at 2 AM every day
    @Transactional
    suspend fun cleanupExpiredTokens() = withContext(Dispatchers.IO) {
        logger.info("Cleaning up expired verification tokens")
        val deleted = userRepository.deleteExpiredVerificationTokens(LocalDateTime.now())
        logger.info("Cleaned up {} expired verification tokens", deleted)
    }
}
