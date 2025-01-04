package com.projecthub.base.comm.application;

import com.projecthub.base.shared.exception.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
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

    public EmailService(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
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
    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            logger.info("Email sent successfully to {}", to);
        } catch (MailException e) {
            logger.error("Failed to send simple email to {}", to, e);
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
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(mimeMessage);
            logger.info("HTML email sent successfully to {}", to);
        } catch (MessagingException e) {
            logger.error("Failed to create or send HTML email to {} due to messaging error", to, e);
            throw new EmailSendingException("Failed to create or send HTML email to " + to + " due to messaging error", e);
        } catch (MailException e) {
            logger.error("Failed to send HTML email to {} due to mail error", to, e);
            throw new EmailSendingException("Failed to send HTML email to " + to + " due to mail error", e);
        }
    }

    /**
     * Sends a templated email asynchronously.
     */
    @Async
    public void sendTemplatedEmail(String to, String subject, String template, Map<String, Object> variables) {
        try {
            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine.process(template, context);
            sendHtmlEmail(to, subject, htmlContent);
        } catch (Exception e) {
            logger.error("Failed to send templated email to {}", to, e);
            throw new EmailSendingException("Failed to send templated email", e);
        }
    }
}
