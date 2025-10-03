package com.kevin.soccertracker.service;

import com.kevin.soccertracker.domain.Match;
import com.kevin.soccertracker.domain.Subscription;
import com.kevin.soccertracker.domain.Team;
import com.kevin.soccertracker.dto.SubscriptionDto;
import com.kevin.soccertracker.repo.SubscriptionRepo;
import com.kevin.soccertracker.repo.TeamRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepo subscriptionRepo;
    private final TeamRepo teamRepo;
    private final MatchService matchService;   // already in your project
    private final EmailService emailService;   // already in your project

    @PersistenceContext
    private EntityManager em;

    /** Admin-triggered reminders by lookahead window in hours. */
    @Transactional(readOnly = true)
    public void sendReminders(int hours) {
        int h = (hours <= 0) ? 3 : hours;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery("""
            SELECT u.email, s.team_id
            FROM subscription s
            JOIN app_user u ON u.id = s.user_id
            WHERE s.active = TRUE
        """).getResultList();

        Map<String, List<Long>> teamIdsByEmail = rows.stream()
                .collect(Collectors.groupingBy(
                        r -> (String) r[0],
                        Collectors.mapping(r -> ((Number) r[1]).longValue(), Collectors.toList())
                ));

        teamIdsByEmail.forEach((email, teamIds) -> {
            List<Match> aggregated = new ArrayList<>();
            for (Long teamId : teamIds) {
                if (teamId != null) {
                    aggregated.addAll(matchService.getUpcomingMatches(teamId, Duration.ofHours(h)));
                }
            }
            if (!aggregated.isEmpty()) {
                emailService.sendReminder(email, aggregated);
            }
        });
    }

    /** Ensure a User row exists (by email), returning its id. */
    @Transactional
    public Long ensureUser(String email) {
        String norm = email.trim().toLowerCase();

        @SuppressWarnings("unchecked")
        List<Number> existing = em.createNativeQuery("""
            SELECT id FROM app_user WHERE email = :e
        """).setParameter("e", norm).getResultList();

        if (!existing.isEmpty()) return existing.get(0).longValue();

        em.createNativeQuery("""
            INSERT INTO app_user (email, name, created_at) VALUES (:e, :n, NOW())
        """)
                .setParameter("e", norm)
                .setParameter("n", norm)
                .executeUpdate();

        Number id = (Number) em.createNativeQuery("""
            SELECT id FROM app_user WHERE email = :e
        """).setParameter("e", norm).getSingleResult();
        return id.longValue();
    }

    /** Idempotent create-or-get subscription by (email, teamId). */
    @Transactional
    public SubscriptionDto createOrGet(String email, Long teamId) {
        Long userId = ensureUser(email);

        var existing = subscriptionRepo.findByUser_IdAndTeam_Id(userId, teamId);
        if (existing.isPresent()) return toDto(existing.get());

        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Team not found: " + teamId));

        try {
            Subscription s = new Subscription();
            s.setUser(em.getReference(com.kevin.soccertracker.domain.User.class, userId));
            s.setTeam(team);
            s.setHoursBefore(2);
            s.setActive(true);
            s.setLastSentAt(null);
            s.setCreatedAt(Instant.now());
            return toDto(subscriptionRepo.saveAndFlush(s));
        } catch (DataIntegrityViolationException race) {
            return subscriptionRepo.findByUser_IdAndTeam_Id(userId, teamId)
                    .map(this::toDto)
                    .orElseThrow(() -> race);
        }
    }

    /** List by email (safe/empty on none). */
    @Transactional(readOnly = true)
    public List<SubscriptionDto> listByEmail(String email) {
        String norm = email.trim().toLowerCase();
        return subscriptionRepo.findByUser_Email(norm)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    /* ===== Mapping ===== */
    private SubscriptionDto toDto(Subscription s) {
        return new SubscriptionDto(
                s.getId(),
                s.getEmail(),   // Subscription has getEmail() convenience
                s.getTeamId()   // and getTeamId()
        );
    }

    /* ===== Local exception ===== */
    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String msg) { super(msg); }
    }
}
