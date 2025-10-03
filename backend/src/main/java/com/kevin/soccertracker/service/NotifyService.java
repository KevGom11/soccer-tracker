package com.kevin.soccertracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class NotifyService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:no-reply@soccertracker.local}")
    private String from;

    public void sendKickoffReminder(String to, String home, String away, String utcIso) {
        var msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject("Kickoff reminder: " + home + " vs " + away);
        msg.setText("""
        Heads up! Kickoff is coming up.

        Match: %s vs %s
        When (UTC): %s

        Boa sorte! ⚽
        """.formatted(home, away, utcIso));
        mailSender.send(msg);
    }
}

