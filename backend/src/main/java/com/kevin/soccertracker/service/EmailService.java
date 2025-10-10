package com.kevin.soccertracker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);


    @Autowired(required = false)
    @Nullable
    private JavaMailSender mailSender;


    @Value("${mail.from:no-reply@soccertracker.local}")
    private String fromAddress;


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


    public void sendPlainText(String to, String subject, String body) {
        send(to, subject, body);
    }


    public void notifyUser(String to, String subject, String body) {
        send(to, subject, body);
    }


    public void sendTest(String to) {
        send(to, "SoccerTracker Test Email", "This is a test email from SoccerTracker.");
    }
}
