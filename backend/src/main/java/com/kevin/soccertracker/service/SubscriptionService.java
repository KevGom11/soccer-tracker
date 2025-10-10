package com.kevin.soccertracker.service;

import com.kevin.soccertracker.domain.Subscription;
import com.kevin.soccertracker.domain.Team;
import com.kevin.soccertracker.domain.User;
import com.kevin.soccertracker.dto.SubscriptionDto;
import com.kevin.soccertracker.repo.SubscriptionRepo;
import com.kevin.soccertracker.repo.TeamRepo;
import com.kevin.soccertracker.security.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepo subscriptionRepo;
    private final TeamRepo teamRepo;

    @Transactional(readOnly = true)
    public List<SubscriptionDto> listForCurrentUser() {
        User u = requireUser();
        return subscriptionRepo.findByUser_Id(u.getId()).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public SubscriptionDto createOrGetForCurrentUser(Long teamId, Integer hoursBefore) {
        User u = requireUser();

        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Team not found: id=" + teamId));

        var existing = subscriptionRepo.findByUser_IdAndTeam_Id(u.getId(), teamId);
        if (existing.isPresent()) {
            Subscription s = existing.get();
            if (hoursBefore != null) s.setHoursBefore(hoursBefore);
            s.setActive(true);
            return toDto(subscriptionRepo.save(s));
        }

        try {
            Subscription s = new Subscription();
            s.setUser(u);
            s.setTeam(team);
            if (hoursBefore != null) s.setHoursBefore(hoursBefore);
            s.setActive(true);
            return toDto(subscriptionRepo.save(s));
        } catch (DataIntegrityViolationException dup) {
            return subscriptionRepo.findByUser_IdAndTeam_Id(u.getId(), teamId)
                    .map(this::toDto)
                    .orElseThrow(() -> dup);
        }
    }

    @Transactional
    public void deleteForCurrentUser(Long subscriptionId) {
        User u = requireUser();
        Subscription s = subscriptionRepo.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Subscription not found: id=" + subscriptionId));
        if (!s.getUser().getId().equals(u.getId())) {
            throw new NotFoundException("Subscription not found for current user");
        }
        subscriptionRepo.delete(s);
    }


    public void sendReminders(int hours) {
        // No-op here; your reminder/notify jobs handle scheduled/triggered sends.
        // If you want manual trigger, wire your ReminderJob/NotifyService here.
    }

    /* ===== Helpers ===== */

    private User requireUser() {
        User u = CurrentUserResolver.get();
        if (u == null) throw new NotSignedInException();
        return u;
    }

    private SubscriptionDto toDto(Subscription s) {
        return new SubscriptionDto(
                s.getId(),
                s.getEmail(),   // convenience getter -> user.getEmail()
                s.getTeamId()   // convenience getter -> team.getId()
        );
    }



    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String msg) { super(msg); }
    }

    public static class NotSignedInException extends RuntimeException {
        public NotSignedInException() { super("Not signed in"); }
    }
}

