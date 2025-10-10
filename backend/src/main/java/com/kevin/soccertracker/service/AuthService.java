package com.kevin.soccertracker.service;

import com.kevin.soccertracker.domain.LoginToken;
import com.kevin.soccertracker.domain.SessionToken;
import com.kevin.soccertracker.domain.User;
import com.kevin.soccertracker.repo.LoginTokenRepo;
import com.kevin.soccertracker.repo.SessionTokenRepo;
import com.kevin.soccertracker.repo.UserRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.Random;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepo userRepo;
    private final LoginTokenRepo loginTokenRepo;
    private final SessionTokenRepo sessionTokenRepo;
    private final EmailService emailService;

    public AuthService(UserRepo userRepo,
                       LoginTokenRepo loginTokenRepo,
                       SessionTokenRepo sessionTokenRepo,
                       EmailService emailService) {
        this.userRepo = userRepo;
        this.loginTokenRepo = loginTokenRepo;
        this.sessionTokenRepo = sessionTokenRepo;
        this.emailService = emailService;
    }

    @Transactional
    public void requestCode(String email) {
        String code = String.format("%06d", new Random().nextInt(1_000_000));
        String hash = sha256(code + "|" + email.toLowerCase());

        LoginToken token = new LoginToken();
        token.setEmail(email);
        token.setCodeHash(hash);
        token.setExpiresAt(OffsetDateTime.now().plusMinutes(10));
        loginTokenRepo.save(token);


        emailService.send(email, "Your SoccerTracker login code",
                "Your code is: " + code + " (valid for 10 minutes)");
    }

    @Transactional
    public SessionToken verify(String email, String code) {
        var latest = loginTokenRepo.findAll().stream()
                .filter(t -> t.getEmail().equalsIgnoreCase(email))
                .max(Comparator.comparing(LoginToken::getCreatedAt))
                .orElseThrow(() -> new IllegalArgumentException("No login code requested"));

        if (latest.getConsumedAt() != null || latest.getExpiresAt().isBefore(OffsetDateTime.now()))
            throw new IllegalArgumentException("Code expired");

        String expected = sha256(code + "|" + email.toLowerCase());
        if (!expected.equals(latest.getCodeHash()))
            throw new IllegalArgumentException("Invalid code");

        latest.setConsumedAt(OffsetDateTime.now());
        loginTokenRepo.save(latest);


        User user = userRepo.findByEmailIgnoreCase(email)
                .orElseGet(() -> userRepo.save(new User(
                        null,              // id
                        email,             // email
                        null,              // name
                        Instant.now()      // createdAt
                )));

        UUID token = UUID.randomUUID();
        SessionToken session = new SessionToken(token, user, OffsetDateTime.now().plusDays(30));
        return sessionTokenRepo.save(session);
    }

    public User resolveUserFromToken(String tokenStr) {
        if (tokenStr == null || tokenStr.isBlank()) return null;
        try {
            UUID token = UUID.fromString(tokenStr);
            return sessionTokenRepo.findByToken(token)
                    .filter(st -> st.getRevokedAt() == null && st.getExpiresAt().isAfter(OffsetDateTime.now()))
                    .map(SessionToken::getUser)
                    .orElse(null);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(s.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
