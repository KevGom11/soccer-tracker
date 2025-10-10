package com.kevin.soccertracker.jobs;

import com.kevin.soccertracker.domain.Subscription;
import com.kevin.soccertracker.repo.SubscriptionRepo;
import com.kevin.soccertracker.service.MatchService;
import com.kevin.soccertracker.service.NotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReminderJob {

    private final SubscriptionRepo subscriptionRepo;
    private final MatchService matchService;
    private final NotifyService notifyService;

    @Value("${app.reminders.enabled:true}")
    private boolean enabled;

    @Value("${app.reminders.hoursBeforeKickoff:3}")
    private int hoursBefore;


    @Scheduled(cron = "0 5 8 * * *")
    public void daily() {
        if (!enabled) return;

        List<Subscription> subs = subscriptionRepo.findAll();
        if (subs.isEmpty()) return;

        // Window: matches starting within the next 'hoursBefore' hours
        var now = ZonedDateTime.now();
        var cutoff = now.plusHours(hoursBefore);


        for (var s : subs) {
            var upcoming = matchService.upcomingMatches(s.getTeamId(), 2); // next 2 days
            upcoming.stream()
                    .filter(m -> m.getUtcDate() != null)
                    .filter(m -> !m.getUtcDate().isBefore(now) && !m.getUtcDate().isAfter(cutoff))
                    .forEach(m -> {

                        System.out.println("REMINDER -> " + s.getEmail() + " : " +
                                m.getHomeTeam().getName() + " vs " + m.getAwayTeam().getName() +
                                " at " + m.getUtcDate());


                    });
        }
    }
}
