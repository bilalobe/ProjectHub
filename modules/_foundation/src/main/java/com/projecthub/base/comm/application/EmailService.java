package com.projecthub.base.comm.application;

import com.projecthub.base.shared.exception.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.Map;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.from}")
    private String fromEmail;

    public EmailService(final JavaMailSender mailSender, final SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Sends a simple email.
     *
     * @param to      Recipient's email address
     * @param subject Subject of the email
     * @param body    Body of the email
     */
    public void sendSimpleEmail(@NonNls final String to, final String subject, final String body) {
        try {
            final SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(this.fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            this.mailSender.send(message);
            EmailService.logger.info("Email sent successfully to {}", to);
        } catch (final MailException e) {
            EmailService.logger.error("Failed to send simple email to {}", to, e);
            throw new EmailSendingException("Failed to send simple email to " + to, e);
        }
    }

    /**
     * Sends an HTML email.
     *
     * @param to       Recipient's email address
     * @param subject  Subject of the email
     * @param htmlBody HTML content of the email
     */
    public void sendHtmlEmail(@NonNls @NonNls @NonNls @NonNls final String to, final String subject, final String htmlBody) {
        try {
            final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(this.fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            this.mailSender.send(mimeMessage);
            EmailService.logger.info("HTML email sent successfully to {}", to);
        } catch (final MessagingException e) {
            EmailService.logger.error("Failed to create or send HTML email to {} due to messaging error", to, e);
            throw new EmailSendingException("Failed to create or send HTML email to " + to + " due to messaging error", e);
        } catch (final MailException e) {
            EmailService.logger.error("Failed to send HTML email to {} due to mail error", to, e);
            throw new EmailSendingException("Failed to send HTML email to " + to + " due to mail error", e);
        }
    }

    /**
     * Sends a templated email asynchronously.
     */
    @Async
    public void sendTemplatedEmail(final String to, final String subject, final String template, final Map<String, Object> variables) {
        try {
            final Context context = new Context();
            context.setVariables(variables);
            final String htmlContent = this.templateEngine.process(template, context);
            this.sendHtmlEmail(to, subject, htmlContent);
        } catch (final RuntimeException e) {
            EmailService.logger.error("Failed to send templated email to {}", to, e);
            throw new EmailSendingException("Failed to send templated email", e);
        }
    }
}
