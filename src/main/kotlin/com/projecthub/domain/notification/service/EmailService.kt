package com.projecthub.domain.notification.service

import com.projecthub.domain.notification.exception.EmailSendingException
import jakarta.mail.MessagingException
import jakarta.mail.internet.MimeMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailException
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine

@Service
class EmailService(
    private val mailSender: JavaMailSender,
    private val templateEngine: SpringTemplateEngine,
    @Value("\${spring.mail.from}") private val fromEmail: String
) {
    private val logger = LoggerFactory.getLogger(EmailService::class.java)

    /**
     * Sends a simple email.
     */
    suspend fun sendSimpleEmail(
        to: String,
        subject: String,
        body: String
    ) = withContext(Dispatchers.IO) {
        try {
            SimpleMailMessage().apply {
                setFrom(fromEmail)
                setTo(to)
                setSubject(subject)
                setText(body)
            }.also {
                mailSender.send(it)
                logger.info("Email sent successfully to {}", to)
            }
        } catch (e: MailException) {
            logger.error("Failed to send simple email to {}", to, e)
            throw EmailSendingException("Failed to send simple email to $to", e)
        }
    }

    /**
     * Sends an HTML email.
     */
    suspend fun sendHtmlEmail(
        to: String,
        subject: String,
        htmlBody: String
    ) = withContext(Dispatchers.IO) {
        try {
            mailSender.createMimeMessage().apply {
                MimeMessageHelper(this, true).apply {
                    setFrom(fromEmail)
                    setTo(to)
                    setSubject(subject)
                    setText(htmlBody, true)
                }
            }.also {
                mailSender.send(it)
                logger.info("HTML email sent successfully to {}", to)
            }
        } catch (e: MessagingException) {
            logger.error("Failed to create or send HTML email to {} due to messaging error", to, e)
            throw EmailSendingException("Failed to create or send HTML email to $to due to messaging error", e)
        } catch (e: MailException) {
            logger.error("Failed to send HTML email to {} due to mail error", to, e)
            throw EmailSendingException("Failed to send HTML email to $to due to mail error", e)
        }
    }

    /**
     * Sends a templated email using coroutines for async operation.
     */
    suspend fun sendTemplatedEmail(
        to: String,
        subject: String,
        template: String,
        variables: Map<String, Any>
    ) = withContext(Dispatchers.IO) {
        try {
            Context().apply {
                setVariables(variables)
            }.let { context ->
                templateEngine.process(template, context)
            }.also { htmlContent ->
                sendHtmlEmail(to, subject, htmlContent)
            }
        } catch (e: RuntimeException) {
            logger.error("Failed to send templated email to {}", to, e)
            throw EmailSendingException("Failed to send templated email", e)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(EmailService::class.java)
    }
}