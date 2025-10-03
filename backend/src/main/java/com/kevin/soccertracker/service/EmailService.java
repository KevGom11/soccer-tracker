package com.kevin.soccertracker.service;

import com.kevin.soccertracker.domain.Match;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:no-reply@example.com}")
    private String from;

    /**
     * Existing reminder email functionality.
     */
    public void sendReminder(String email, List<Match> matches) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(email);
        msg.setSubject("Upcoming matches reminder");

        StringBuilder body = new StringBuilder("You have upcoming matches:\n\n");
        for (Match m : matches) {
            body.append("%s vs %s at %s%n".formatted(
                    m.getHomeTeam().getName(),
                    m.getAwayTeam().getName(),
                    m.getUtcDate()
            ));
        }

        msg.setText(body.toString());
        mailSender.send(msg);
    }

    /**
     * New: Simple sanity-test email so we can verify SMTP end-to-end.
     */
    public void sendTest(String to) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject("SoccerTracker SMTP Test");
        msg.setText("""
                Hello!

                This is a test email from SoccerTracker to confirm your SMTP configuration.
                If you received this, outgoing mail is working.

                Timestamp: %s
                """.formatted(ZonedDateTime.now()));

        mailSender.send(msg);
    }
}
