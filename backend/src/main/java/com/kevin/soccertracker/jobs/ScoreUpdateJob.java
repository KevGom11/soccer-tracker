package com.kevin.soccertracker.jobs;

import com.kevin.soccertracker.repo.SubscriptionRepo;
import com.kevin.soccertracker.service.MatchSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScoreUpdateJob {

    private final SubscriptionRepo subscriptionRepo;
    private final MatchSyncService matchSyncService;

    @Value("${fetcher.enabled:true}")
    private boolean enabled;

    @Value("${fetcher.windowDays:2}")
    private int windowDays;

    @Value("${fetcher.includeActive:true}")
    private boolean includeActive;

    @Value("${fetcher.minIntervalSeconds:1800}") // 30 min per team
    private long minIntervalSeconds;

    @Value("${fetcher.maxTeamsPerRun:9}") // keep under 10/min
    private int maxTeamsPerRun;

    @Value("${fetcher.spacingMillisBetweenCalls:6500}") // ~9 calls/min
    private long spacingMillisBetweenCalls;

    // last fetch per team (to avoid re-polling too frequently)
    private final ConcurrentHashMap<Long, Instant> lastFetch = new ConcurrentHashMap<>();

    /** Cron configured in application.yml (default: every minute). */
    @Scheduled(cron = "${fetcher.cron:0 * * * * *}")
    public void run() {
        if (!enabled) return;

        final List<Long> allTeamIds = subscriptionRepo.findAll().stream()
                .map(s -> s.getTeamId())
                .filter(id -> id != null)
                .distinct()
                .toList();

        if (allTeamIds.isEmpty()) {
            log.debug("ScoreUpdateJob: no subscribed teams, skipping");
            return;
        }

        final Instant now = Instant.now();

        // Only teams that haven't been fetched too recently
        final Set<Long> candidates = allTeamIds.stream()
                .filter(teamId -> shouldFetch(teamId, now))
                .limit(Math.max(1, maxTeamsPerRun))
                .collect(Collectors.toSet());

        if (candidates.isEmpty()) {
            log.debug("ScoreUpdateJob: no candidates (all within cool-down)");
            return;
        }

        final LocalDate from = LocalDate.now(ZoneOffset.UTC);
        final LocalDate to = from.plusDays(Math.max(1, windowDays));

        int totalUpdated = 0;
        int callsMade = 0;

        for (Long teamId : candidates) {
            try {
                int updated = matchSyncService.syncTeamWindow(teamId, from, to, includeActive);
                totalUpdated += updated;
                callsMade++;

                lastFetch.put(teamId, now);

                // pace between calls to stay below 10/min
                if (callsMade < candidates.size() && spacingMillisBetweenCalls > 0) {
                    try {
                        Thread.sleep(spacingMillisBetweenCalls);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            } catch (Exception e) {
                log.warn("ScoreUpdateJob: team={} failed: {}", teamId, e.toString());
            }
        }

        log.info("ScoreUpdateJob: fetched {} team(s), updated {} match row(s)", callsMade, totalUpdated);
    }

    private boolean shouldFetch(Long teamId, Instant now) {
        Instant last = lastFetch.get(teamId);
        if (last == null) return true;
        long elapsed = now.getEpochSecond() - last.getEpochSecond();
        return elapsed >= minIntervalSeconds;
    }
}
