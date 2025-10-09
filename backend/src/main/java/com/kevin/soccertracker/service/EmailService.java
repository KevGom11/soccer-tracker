package com.kevin.soccertracker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Minimal email utility.
 * - If JavaMailSender is available, sends real emails.
 * - Otherwise, logs messages so OTP/test flows work in dev without SMTP.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    /** Injected only if an SMTP sender is configured (spring-boot-starter-mail). */
    @Autowired(required = false)
    @Nullable
    private JavaMailSender mailSender;

    /** From address for real sends (fallback is dev-safe). */
    @Value("${mail.from:no-reply@soccertracker.local}")
    private String fromAddress;

    /** Core plain-text send used by AuthService and others. */
    public void send(String to, String subject, String body) {
        if (mailSender == null) {
            log.info("MAIL (DEV LOG) -> to='{}' subject='{}' body='{}'", to, subject, body);
            return;
        }
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromAddress);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
        log.debug("MAIL (SENT) -> to='{}' subject='{}'", to, subject);
    }

    /** Convenience alias some code may already use. */
    public void sendPlainText(String to, String subject, String body) {
        send(to, subject, body);
    }

    /** Another alias for generic notifications. */
    public void notifyUser(String to, String subject, String body) {
        send(to, subject, body);
    }

    /** ✅ Added for AdminController: sends a simple test email (or logs in dev). */
    public void sendTest(String to) {
        send(to, "SoccerTracker Test Email", "This is a test email from SoccerTracker.");
    }
}
